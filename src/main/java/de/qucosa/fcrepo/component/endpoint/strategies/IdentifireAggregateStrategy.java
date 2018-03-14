package de.qucosa.fcrepo.component.endpoint.strategies;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class IdentifireAggregateStrategy implements AggregationStrategy {

    public IdentifireAggregateStrategy() {
        super();
    }
    
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        System.out.println("OE: " + oldExchange.getIn().getBody());
        System.out.println("NE: " + newExchange.getIn().getBody());
        return null;
    }
}
