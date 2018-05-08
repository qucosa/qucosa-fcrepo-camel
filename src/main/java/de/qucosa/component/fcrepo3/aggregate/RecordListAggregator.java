/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.component.fcrepo3.aggregate;

import de.qucosa.component.oaiprovider.model.RecordTransport;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecordListAggregator implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if (oldExchange == null || oldExchange.getIn().getBody() == null) {
            return newExchange;
        }

        if (newExchange == null || newExchange.getIn().getBody() == null) {
            return oldExchange;
        }

        combineRecords(oldExchange, newExchange);

        return newExchange;
    }

    private void combineRecords(Exchange eOld, Exchange eNew) {

        if (eOld.getIn().getBody() instanceof  RecordTransport && eNew.getIn().getBody() instanceof RecordTransport) {
            List<RecordTransport> records = new ArrayList<>();
            records.add(eOld.getIn().getBody(RecordTransport.class));
            records.add(eNew.getIn().getBody(RecordTransport.class));
            eNew.getIn().setBody(records);
        }

        if (eOld.getIn().getBody() instanceof List && eNew.getIn().getBody() instanceof RecordTransport) {
            List<RecordTransport> records = new ArrayList<>((Collection<? extends RecordTransport>) eOld.getIn().getBody());
            records.add(eNew.getIn().getBody(RecordTransport.class));
            eNew.getIn().setBody(records);
        }

        if (eOld.getIn().getBody() instanceof RecordTransport && eNew.getIn().getBody() instanceof List) {
            ((List) eNew.getIn().getBody()).add(eOld.getIn().getBody(RecordTransport.class));
        }

        if (eOld.getIn().getBody() instanceof List && eNew.getIn().getBody() instanceof List) {
            ((List) eOld.getIn().getBody()).addAll((Collection) eOld.getIn().getBody());
        }
    }
}
