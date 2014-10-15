/**
 * Copyright 2014 Unicon (R)
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
package od;

import od.model.Card;
import od.model.CardType;
import od.model.repository.CardInstanceRepository;
import od.model.repository.CardRepository;
import od.model.repository.ContextMappingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("od")
@Configuration
@EnableAutoConfiguration
public class Application {

    final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(Application.class, args);
    	
        // test data
        CardRepository cardRepository = ctx.getBean(CardRepository.class);
        ContextMappingRepository contextMappingRepository = ctx.getBean(ContextMappingRepository.class);
        CardInstanceRepository cardInstanceRepository = ctx.getBean(CardInstanceRepository.class);
        cardRepository.deleteAll();
        contextMappingRepository.deleteAll();
        cardInstanceRepository.deleteAll();
        
		Card card1 = new Card();
		card1.setCardType(CardType.LTI);
		card1.setName("LTI");
		card1.setDescription("Use this card to launch out to an LTI tool");
		card1.setImgUrl("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQ8F5Og94mVZUAFy7fMqcmv5NZUJMqH8j0FcgvFzete2Z5YJClgDQ");
		
		Card card2 = new Card();
		card2.setCardType(CardType.openlrs);
		card2.setName("OpenLRS");
		card2.setDescription("Use this card to access your course data in OpenLRS");
		card2.setImgUrl("/img/openlrs.png");

		cardRepository.save(card1);
		cardRepository.save(card2);
    }

}
