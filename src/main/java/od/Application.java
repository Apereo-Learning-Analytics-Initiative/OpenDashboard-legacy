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

import java.io.IOException;

import od.model.Card;
import od.model.repository.CardRepository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan("od")
@Configuration
@EnableAutoConfiguration
public class Application {

    final static Logger log = LoggerFactory.getLogger(Application.class);
    
    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(Application.class, args);
    	CardRepository cardRepository = ctx.getBean(CardRepository.class);
    	
    	try {
    		Resource cardListResource = ctx.getResource("classpath:cards/cards.txt");
			String cardList = new String(FileCopyUtils.copyToByteArray(cardListResource.getInputStream()));
			log.info("Available cards: "+cardList);
			
			String [] cards = StringUtils.split(cardList, ",");
			ObjectMapper objectMapper = new ObjectMapper();
			for (String c : cards) {
				Resource cardResource = ctx.getResource("classpath:cards/"+c+".json");
				Card card = objectMapper.readValue(cardResource.getInputStream(), Card.class);
				Card cardExists = cardRepository.findByCardType(card.getCardType());
				if (cardExists == null) {
					log.info("Adding card: "+card.getName());
					cardRepository.save(card);
				}
			}
			
		} 
    	catch (IOException e) {
			log.error(e.getMessage(),e);
		}    	
    }
}
