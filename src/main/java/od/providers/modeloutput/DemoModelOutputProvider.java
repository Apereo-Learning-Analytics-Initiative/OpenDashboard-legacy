package od.providers.modeloutput;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.DatatypeConverter;

import od.providers.ProviderData;
import od.providers.ProviderException;
import od.providers.config.ProviderConfiguration;
import od.providers.config.ProviderConfigurationOption;

import org.apereo.lai.ModelOutput;
import org.apereo.lai.impl.ModelOutputImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import unicon.matthews.oneroster.Enrollment;
import unicon.matthews.oneroster.Role;
import unicon.matthews.oneroster.Status;
import unicon.matthews.oneroster.User;

@Component("modeloutput_demo")
public class DemoModelOutputProvider implements ModelOutputProvider {

  private static final String KEY = "modeloutput_demo";
  private static final String BASE = "OD_DEMO_MODELOUTPUT";
  private static final String NAME = String.format("%s_NAME", BASE);
  private static final String DESC = String.format("%s_DESC", BASE);
  
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

  @Override
  public Page<ModelOutput> getModelOutputForContext(ProviderData providerData, String tenantId, String contextId, Pageable page)
      throws ProviderException {
    
    List<ModelOutput> modelOutput = new ArrayList<>();
    
    for (int s = 0; s < 60; s++) {
      String studentSourcedId = "demo-student-".concat(String.valueOf(s));
      Map<String, Object> outputParams = new HashMap<>();
      outputParams.put("ALTERNATIVE_ID", studentSourcedId);
      outputParams.put("CLASS_SOURCED_ID", contextId);
      outputParams.put("RISK_SCORE", 50);
      
      ModelOutputImpl output = new ModelOutputImpl(outputParams,new Date());
      output.setUserSourcedId(studentSourcedId);
      modelOutput.add(output);
    }

    
    
    return new PageImpl<>(modelOutput);
  }

}
