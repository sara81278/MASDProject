threshold(150).
budget(5000).

    
+proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), est_down(Est_down), 
    response(Response))[source(A)]:  budget(Budget) & Budget > 0 & threshold(Threshold) 
    & (Price - Est_down) <= Threshold & A == trading
<- .wait(5000);
    .println("Received proposal: name = ", Name, ", type = ", Type, " Quantity to buy = ", 
            Response, " Price = ", Price);
    .println("Proposal met the conditions. Checking if it already present in the database.");
    check_if_present("src/resources/proposals_saved.csv", Name, Type, Price, Est_up, Est_down, Response).

+proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), est_down(Est_down), 
    response(Response))[source(A)]: (budget(Budget) & Budget < 0) | threshold(Threshold) 
    & (Price - Est_down) > Threshold & A == trading
<- .wait(5000);
    .println("Received proposal: name = ", Name, ", type = ", Type, " Quantity to buy = ", 
            Response, " Price = ", Price);
    .println("Proposal rejected by Risk Agent!");
    .send(trading, tell, proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), 
        est_down(Est_down), response(0))).

+proposal_response(Name, Type, Price, Est_up, Est_down,Response)[source(A)]: Response > 0 & A \==trading
<- .wait(5000);
    .println("Proposal not present in the database. Saving it!");
   save_proposal("src/resources/proposals_saved.csv", Name, Type, Price, Est_up, Est_down, Response).

+proposal_response(Name, Type, Price, Est_up, Est_down,Response)[source(A)]: Response <= 0 & A \== trading
<-  .wait(5000);
    .println("Proposal already in the database, rejected!");
    .send(trading, tell, proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), 
        est_down(Est_down), response(Response))).

