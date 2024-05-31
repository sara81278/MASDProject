!start.

+!start
<- .println("Reading proposal from CSV file...");
   !read_proposal_from_file("src/resources/proposals.csv").

+!read_proposal_from_file(FilePath)
<- read_proposal(FilePath).


+proposal(Name, Type, Price, Est_up, Est_down)
<-  .wait(5000);
    .println("Received proposal: name = ", Name, ", type = ", Type, ", price = ", Price, 
    ", Estimated increase = ", Est_up, ", Estimated decrease = ", Est_down);
    Proposal = proposal(name(Name), type(Type), price(Price), est_up(Est_up), est_down(Est_down));
    .send(portfolio, tell, object(Proposal));
    .println("Sending proposal to portfolio agent").

+proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), est_down(Est_down), 
    response(Response))[source(A)]: Response == 0 
<-  .wait(5000);
    .println("Received response for proposal: name = ", Name, ", type = ", Type);
    .println("Proposal rejected by ", A, " agent").

+proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), est_down(Est_down), 
    response(Response))[source(A)]: Response \== 0
<-  .wait(5000);
    .println("Received response for proposal: name = ", Name, ", type = ", Type, ", Price = ", Price);
    .println("Proposal accepted by ", A, " agent");
    .send(risk, tell, proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), 
        est_down(Est_down), response(Response))).
