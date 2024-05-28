package example;

// Environment code for project masd_project

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.*;

public class Env extends Environment {

    private Logger logger = Logger.getLogger("masd_project."+Env.class.getName());

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args){
        try {
            super.init(args);
            //addPercept(ASSyntax.parseLiteral("percept("+args[0]+")"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        if (action.getFunctor().equals("read_proposal")) {
            try {
                Term filePathTerm = action.getTerm(0);
                if (filePathTerm instanceof StringTerm) {
                    String filePath = ((StringTerm) filePathTerm).getString();
                    Literal proposal = readProposal(filePath);

                    if (proposal != null) {
                        addPercept(agName, proposal);
                        return true;
                    }
                }
            } catch (Exception e) {
                logger.severe("Failed to read proposal: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else if (action.getFunctor().equals("save_proposal")) {
            try {
                Term filePathTerm = action.getTerm(0);
                if (filePathTerm instanceof StringTerm) {
                    String filePath = ((StringTerm) filePathTerm).getString();
                    String name = action.getTerm(1).toString();
                    String type = action.getTerm(2).toString();
                    double price = Double.parseDouble(action.getTerm(3).toString());
                    double est_up = Double.parseDouble(action.getTerm(4).toString());
                    double est_down = Double.parseDouble(action.getTerm(5).toString());
                    double response = Double.parseDouble(action.getTerm(6).toString());
                    ProposalResponse proposal = new ProposalResponse(name, type, price, est_up, est_down, response);

                    saveProposal(filePath, proposal);

                    return true;
                }
            } catch (Exception e) {
                logger.severe("Failed to save proposal: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else if (action.getFunctor().equals("check_if_present")) {
            try {

                Term filePathTerm = action.getTerm(0);
                if (filePathTerm instanceof StringTerm) {
                    String filePath = ((StringTerm) filePathTerm).getString();
                    String name = action.getTerm(1).toString();
                    String type = action.getTerm(2).toString();
                    double price = Double.parseDouble(action.getTerm(3).toString());
                    double est_up = Double.parseDouble(action.getTerm(4).toString());
                    double est_down = Double.parseDouble(action.getTerm(5).toString());
                    double response = Double.parseDouble(action.getTerm(6).toString());
                    ProposalResponse proposal = new ProposalResponse(name, type, price, est_up, est_down, response);

                    Literal proposal_response = checkIfPresent(filePath, proposal);


                    
                    if (proposal_response != null) {
                        addPercept(agName, proposal_response);
                        return true;
                    }

                }
            } catch (Exception e) {
                logger.severe("Failed to save proposal: " + e.getMessage());
                e.printStackTrace();
            }

        }
        return false;
    }

    private Literal readProposal(String filePath) {
        String line;
        String csvSplitBy = ",";
        List<Proposal> proposalList = new ArrayList<Proposal>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                String name = data[0];
                String type = data[1];
                double price = Double.parseDouble(data[2]);
                double est_up = Double.parseDouble(data[3]);
                double est_down = Double.parseDouble(data[4]);
                proposalList.add(new Proposal(name, type, price, est_up, est_down));
            }
            Proposal chosenProposal = proposalList.get((new Random()).nextInt(0, proposalList.size()));
            return Literal.parseLiteral("proposal(" + chosenProposal.getName() + "," + chosenProposal.getType() + "," + chosenProposal.getPrice() + ","+ chosenProposal.getEstUp() + ","+ chosenProposal.getEstDown()+ ")");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in reading csv file: "+filePath, e);
        }
        return null;
    }

    private Literal checkIfPresent(String filePath, ProposalResponse proposal) {
        String line;
        String csvSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                String name = data[0];
                String type = data[1];
                if(proposal.getName().equals(name) && proposal.getType().equals(type)) {
                    return Literal.parseLiteral("proposal_response(" + proposal.getName() + "," + proposal.getType() + "," + proposal.getPrice() + "," + proposal.getEstUp() + ","+ proposal.getEstDown() + ","+ 0.0+")");
                }
            }
            return Literal.parseLiteral("proposal_response(" + proposal.getName() + "," + proposal.getType() + "," + proposal.getPrice() + "," + proposal.getEstUp() + ","+ proposal.getEstDown() + ","+ proposal.getResponse()+")");
        } catch (IOException e) {
            logger.log(Level.INFO, "Error in reading csv file: "+filePath, e);
        }
        return null;
    }

    private void saveProposal(String filePath, ProposalResponse proposal) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append(proposal.getName()).append(',')
                  .append(proposal.getType()).append(',')
                  .append(proposal.getPrice()+"").append(',')
                  .append(proposal.getEstUp()+"").append(',')
                  .append(proposal.getEstDown()+"").append(',')
                  .append(proposal.getResponse()+"").append('\n');
        } catch (IOException e) {
            logger.warning("Error writing to CSV file: " + e.getMessage());
        }
    }


    class Proposal{
        private String name;
        private String type;
        private double price;
        private double est_up;
        private double est_down;

        public Proposal(String name, String type, double price, double est_up, double est_down) {
            this.name = name;
            this.type = type;
            this.price = price;
            this.est_up = est_up;
            this.est_down = est_down;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public double getPrice() {
            return price;
        }

        public double getEstUp() {
            return est_up;
        }

        public double getEstDown() {
            return est_down;
        }
    }

    class ProposalResponse extends Proposal {

        private double response;

        public ProposalResponse(String name, String type, double price, double est_up, double est_down, double response) {
            super(name, type, price, est_up, est_down);
            this.response = response;
        }

        public double getResponse() {
            return response;
        }

    }
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}

