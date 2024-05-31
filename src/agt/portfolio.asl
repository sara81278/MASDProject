percentage_gain(0.15).
budget(5000).
percentage_budget(0.05).

+object(proposal(name(Name), type(Type), price(Price), est_up(Est_up), est_down(Est_down)))[source(A)] 
    <- .wait(5000);
    .println("Received proposal: Name=", Name, ", Type=", Type, ", from ", A);
       !check_value(Name, Type, Price, Est_up, Est_down, A).

+!check_value(Name, Type, Price, Est_up, Est_down, A) : budget(Budget) & Budget > 0 
    & percentage_gain(PercentageGain) & (Est_up - Price) >= (PercentageGain*Price) 
& percentage_budget(PercentageBudget)
<-  .wait(5000);
    .println("Conditions met. Accepting proposal: Name=", Name, ", Type=", Type, ", from ", A);
    Y = (Budget*PercentageBudget)/Price;
    .send(A, tell, proposal_response(name(Name), type(Type), price(Price), est_up(Est_up), 
        est_down(Est_down), response(Y))).

+!check_value(Name, Type, Price, Est_up, Est_down, A) : (budget(Budget) & Budget <= 0) | 
    (percentage_gain(PercentageGain) & (Est_up - Price) < (PercentageGain*Price))
<- .wait(5000);
    .println("Proposal not accepted: Name=", Name, ", Type=", Type);
   .send(A, tell, proposal_response(name(Name), type(Type), price(Price), est_up(Est_up),
    est_down(Est_down), response(0))).

