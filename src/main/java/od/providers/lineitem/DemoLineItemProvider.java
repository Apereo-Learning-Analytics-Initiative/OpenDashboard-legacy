/**
 * 
 */
package od.providers.lineitem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.PostConstruct;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import unicon.matthews.oneroster.LineItem;
import unicon.matthews.oneroster.LineItemCategory;
import unicon.matthews.oneroster.Status;
import unicon.oneroster.Vocabulary;

/**
 * @author ggilbert
 *
 */
@Component("lineitem_demo")
public class DemoLineItemProvider implements LineItemProvider {


  private static final Logger log = LoggerFactory.getLogger(DemoLineItemProvider.class);
  
  private static final String KEY = "lineitem_demo";
  private static final String BASE = "OD_DEMO_LINEITEMS";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
  private Map<String, Set<LineItem>> classLineItemMap;
  
  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDesc() {
    return DESC;
  }

  @Override
  public ProviderConfiguration getProviderConfiguration() {
    // Not needed for demo provider
    return new ProviderConfiguration() {
      
      @Override
      public LinkedList<ProviderConfigurationOption> getOptions() {
        return new LinkedList<>();
      }
      
      @Override
      public ProviderConfigurationOption getByKey(String key) {
        return null;
      }
    };
  }
  
  @PostConstruct
  public void init() {
    
    Set<LineItem> demoClass1LineItems = new HashSet<>();
    Set<LineItem> demoClass2LineItems = new HashSet<>();
    Set<LineItem> demoClass3LineItems = new HashSet<>();
    
    classLineItemMap = new HashMap<>();
    classLineItemMap.put("demo-class-1", demoClass1LineItems);
    classLineItemMap.put("demo-class-2", demoClass2LineItems);
    classLineItemMap.put("demo-class-3", demoClass3LineItems);
    
    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    
    String [] classSourcedIds = {"demo-class-1","demo-class-2","demo-class-3"};
    
    Map<String, unicon.matthews.oneroster.Class> classes = new HashMap<>();
    
    Map<String, String> metadata1 = new HashMap<>();   
    metadata1.put(Vocabulary.CLASS_START_DATE, LocalDate.of(2017, 1, 23).toString());
    metadata1.put(Vocabulary.CLASS_END_DATE, LocalDate.of(2017, 5, 11).toString());
    metadata1.put(Vocabulary.SOURCE_SYSTEM, "DEMO");
    
    Map<String, String> metadata2 = new HashMap<>();
    metadata2.put(Vocabulary.CLASS_START_DATE, LocalDate.of(2017, 1, 18).toString());
    metadata2.put(Vocabulary.CLASS_END_DATE, LocalDate.of(2017, 5, 10).toString());
    metadata2.put(Vocabulary.SOURCE_SYSTEM, "DEMO");

    Map<String, String> metadata3 = new HashMap<>();
    metadata3.put(Vocabulary.CLASS_START_DATE, LocalDate.of(2017, 1, 28).toString());
    metadata3.put(Vocabulary.CLASS_END_DATE, LocalDate.of(2017, 5, 27).toString());
    metadata3.put(Vocabulary.SOURCE_SYSTEM, "DEMO");
    
    unicon.matthews.oneroster.Class class1
      = new unicon.matthews.oneroster.Class.Builder()
          .withSourcedId("demo-class-1")
          .withTitle("Introduction to Organic Chemistry")
          .withMetadata(metadata1)
          .withStatus(Status.active)
          .build();
    
    unicon.matthews.oneroster.Class class2
      = new unicon.matthews.oneroster.Class.Builder()
        .withSourcedId("demo-class-2")
        .withTitle("Advanced Chemistry 303")
        .withMetadata(metadata2)
        .withStatus(Status.active)
        .build();

    unicon.matthews.oneroster.Class class3
      = new unicon.matthews.oneroster.Class.Builder()
        .withSourcedId("demo-class-3")
        .withTitle("MicroBiology 201")
        .withMetadata(metadata3)
        .withStatus(Status.active)
        .build();
    
    classes.put("demo-class-1", class1);
    classes.put("demo-class-2", class2);
    classes.put("demo-class-3", class3);
    
    LineItemCategory quiz 
    = new LineItemCategory.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("quiz")
      .build();

    LineItemCategory exam 
    = new LineItemCategory.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("exam")
      .build();

    LineItemCategory lab 
    = new LineItemCategory.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("lab")
      .build();

    LineItemCategory report 
    = new LineItemCategory.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("report")
      .build();

    
    LineItem class1_li0 
      = new LineItem.Builder()
        .withSourcedId(UUID.randomUUID().toString())
        .withStatus(Status.active)
        .withTitle("Hands-on Lab")
        .withDescription("Learn how to use the microscope.")
        .withAssignDate(LocalDateTime.of(2017, 2, 7, 0, 0))
        .withDueDate(LocalDateTime.of(2017, 2, 14, 0, 0))
        .withClass(class1)
        .withCategory(lab)
        .build();
    
    LineItem class1_li1 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Lab Report")
      .withDescription("Report on observations")
      .withAssignDate(LocalDateTime.of(2017, 3, 7,0,0))
      .withDueDate(LocalDateTime.of(2017, 4, 1,0,0))
      .withClass(class1)
      .withCategory(report)
      .build();
   
    LineItem class1_li2 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Midterm Exam")
      .withDescription("Good luck!")
      .withAssignDate(LocalDateTime.of(2017, 3, 1,0,0))
      .withDueDate(LocalDateTime.of(2017, 3, 27,0,0))
      .withClass(class1)
      .withCategory(exam)
      .build();
    
    LineItem class1_li3 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Quiz #1")
      .withAssignDate(LocalDateTime.of(2017, 4, 15,0,0))
      .withDueDate(LocalDateTime.of(2017, 4, 15,0,0))
      .withClass(class1)
      .withCategory(quiz)
      .build();

    LineItem class1_li4 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Final Exam")
      .withDescription("Enjoy break!")
      .withAssignDate(LocalDateTime.of(2017, 2, 1,0,0))
      .withDueDate(LocalDateTime.of(2017, 2, 10,0,0))
      .withClass(class1)
      .withCategory(exam)
      .build();
    
    demoClass1LineItems.add(class1_li0);
    demoClass1LineItems.add(class1_li1);
    demoClass1LineItems.add(class1_li2);
    demoClass1LineItems.add(class1_li3);
    demoClass1LineItems.add(class1_li4);
    
    LineItem class2_li0 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Hands-on Lab")
      .withDescription("Compounds and more!")
      .withAssignDate(LocalDateTime.of(2017, 3, 2,0,0))
      .withDueDate(LocalDateTime.of(2017, 3, 19,0,0))
      .withClass(class2)
      .withCategory(lab)
      .build();
  
  LineItem class2_li1 
  = new LineItem.Builder()
    .withSourcedId(UUID.randomUUID().toString())
    .withStatus(Status.active)
    .withTitle("Lab Report")
    .withDescription("Report on compounds")
    .withAssignDate(LocalDateTime.of(2017, 4, 2,0,0))
    .withDueDate(LocalDateTime.of(2017, 4, 15,0,0))
    .withClass(class2)
    .withCategory(report)
    .build();
    
    LineItem class2_li11 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Quiz #1")
      .withDescription("Knowledge check")
      .withAssignDate(LocalDateTime.of(2017, 4, 2,0,0))
      .withDueDate(LocalDateTime.of(2017, 4, 19,0,0))
      .withClass(class2)
      .withCategory(report)
      .build();

 
  LineItem class2_li2 
  = new LineItem.Builder()
    .withSourcedId(UUID.randomUUID().toString())
    .withStatus(Status.active)
    .withTitle("Midterm Exam")
    .withDescription("Good luck!")
    .withAssignDate(LocalDateTime.of(2017, 4, 2,0,0))
    .withDueDate(LocalDateTime.of(2017, 4, 30,0,0))
    .withClass(class2)
    .withCategory(exam)
    .build();
  
  LineItem class2_li3 
  = new LineItem.Builder()
    .withSourcedId(UUID.randomUUID().toString())
    .withStatus(Status.active)
    .withTitle("Quiz #2")
    .withAssignDate(LocalDateTime.of(2017, 3, 15,0,0))
    .withDueDate(LocalDateTime.of(2017, 3, 19,0,0))
    .withClass(class2)
    .withCategory(quiz)
    .build();

  LineItem class2_li4 
  = new LineItem.Builder()
    .withSourcedId(UUID.randomUUID().toString())
    .withStatus(Status.active)
    .withTitle("Final Exam")
    .withDescription("Enjoy break!")
    .withAssignDate(LocalDateTime.of(2017, 4, 2,0,0))
    .withDueDate(LocalDateTime.of(2017, 4, 6,0,0))
    .withClass(class2)
    .withCategory(exam)
    .build();
  
  demoClass2LineItems.add(class2_li0);
  demoClass2LineItems.add(class2_li1);
  demoClass2LineItems.add(class2_li11);
  demoClass2LineItems.add(class2_li2);
  demoClass2LineItems.add(class2_li3);
  demoClass2LineItems.add(class2_li4);

  LineItem class3_li0 
  = new LineItem.Builder()
    .withSourcedId(UUID.randomUUID().toString())
    .withStatus(Status.active)
    .withTitle("Hands-on Lab")
    .withDescription("Disection! The innards of ficus.")
    .withAssignDate(LocalDateTime.of(2017, 2, 10,0,0))
    .withDueDate(LocalDateTime.of(2017, 2, 15,0,0))
    .withClass(class3)
    .withCategory(lab)
    .build();

LineItem class3_li1 
= new LineItem.Builder()
  .withSourcedId(UUID.randomUUID().toString())
  .withStatus(Status.active)
  .withTitle("Lab Report")
  .withDescription("Report on discection")
  .withAssignDate(LocalDateTime.of(2017, 3, 10,0,0))
  .withDueDate(LocalDateTime.of(2017, 4, 1,0,0))
  .withClass(class3)
  .withCategory(report)
  .build();
  
  LineItem class3_li11 
  = new LineItem.Builder()
    .withSourcedId(UUID.randomUUID().toString())
    .withStatus(Status.active)
    .withTitle("Quiz #1")
    .withDescription("Knowledge check")
    .withAssignDate(LocalDateTime.of(2017, 3, 19,0,0))
    .withDueDate(LocalDateTime.of(2017, 4, 17,0,0))
    .withClass(class3)
    .withCategory(report)
    .build();


    LineItem class3_li2 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Midterm Exam")
      .withDescription("Good luck!")
      .withAssignDate(LocalDateTime.of(2017, 3, 17,0,0))
      .withDueDate(LocalDateTime.of(2017, 4, 1,0,0))
      .withClass(class3)
      .withCategory(exam)
      .build();
    
    LineItem class3_li3 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Quiz #2")
      .withAssignDate(LocalDateTime.of(2017, 4, 15,0,0))
      .withDueDate(LocalDateTime.of(2017, 4, 15,0,0))
      .withClass(class3)
      .withCategory(quiz)
      .build();
    
    LineItem class3_li4 
    = new LineItem.Builder()
      .withSourcedId(UUID.randomUUID().toString())
      .withStatus(Status.active)
      .withTitle("Final Exam")
      .withDescription("Enjoy break!")
      .withAssignDate(LocalDateTime.of(2017, 4, 7, 0, 0))
      .withDueDate(LocalDateTime.of(2017, 4, 9, 0, 0))
      .withClass(class3)
      .withCategory(exam)
      .build();

    demoClass3LineItems.add(class3_li0);
    demoClass3LineItems.add(class3_li1);
    demoClass3LineItems.add(class3_li11);
    demoClass3LineItems.add(class3_li2);
    demoClass3LineItems.add(class3_li3);
    demoClass3LineItems.add(class3_li4);

  }


  @Override
  public Set<LineItem> getLineItemsForClass(ProviderData providerData, String classSourcedId) throws ProviderException {
    return classLineItemMap.get(classSourcedId);
  }

}
