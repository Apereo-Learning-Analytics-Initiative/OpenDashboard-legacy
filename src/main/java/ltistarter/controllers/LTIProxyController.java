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
package ltistarter.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import ltistarter.exceptions.EntityNotFoundException;
import ltistarter.model.LaunchForm;
import ltistarter.model.LaunchRequest;
import ltistarter.model.LtiProxyConfig;
import ltistarter.oauth.OAuthMessageSigner;
import ltistarter.oauth.OAuthUtil;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for displaying OpenLRS view.
 */
@Controller
@RequestMapping("/ltiproxy")
public class LTIProxyController extends BaseController {

    private OAuthMessageSigner signer = new OAuthMessageSigner();
    private Map<String,LtiProxyConfig> launchConfigMap = new HashMap<String,LtiProxyConfig>();

    @RequestMapping({"", "/"})
    @Secured("ROLE_LTI")
    public String ltiproxy(HttpServletRequest req, Principal principal, Model model) {
        return "ltiproxy";
    }

    @RequestMapping({"", "/launch"})
//    @Secured("ROLE_LTI")
    public String launch(HttpServletRequest req, Principal principal, Model model) {
        log.debug("PRINCIPAL: {}", principal);
//        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)principal;
//        final LTIUserCredentials ltiOauthPrincipal = (LTIUserCredentials)token.getCredentials();
        final LtiProxyConfig config = this.lookupLtiLaunchConfig("admin", "mercury");//ltiOauthPrincipal.getUserId(), ltiOauthPrincipal.getContextId());
        if (config == null) {
            throw new IllegalStateException("cannot launch without configured LTI proxy");
        } else {
            final LaunchRequest launchRequest = this.createLaunchRequest(config);
            final String signature = this.calculateOauthSignature(config, launchRequest);
            final LaunchForm launchForm = new LaunchForm(config.getUrl(), false, launchRequest.toSortedMap(), signature);
            model.addAttribute("launchForm", launchForm);
            return "ltilaunch";
        }
    }

    @RequestMapping(value = "config", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    //@Secured("ROLE_LTI")
    public LtiProxyConfig getConfig(HttpServletRequest req, Principal principal) {
//        log.debug("PRINCIPAL: {}", principal);
//        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)principal;
//        final LTIUserCredentials ltiOauthPrincipal = (LTIUserCredentials)token.getCredentials();
        final LtiProxyConfig config = this.lookupLtiLaunchConfig("admin", "mercury");//ltiOauthPrincipal.getUserId(), ltiOauthPrincipal.getContextId());
        if (config == null) {
            throw new EntityNotFoundException("LtiProxyConfig for [userId: " + "admin" + ", contextId: " + "mercury" + "]");
        } else {
            return config;
        }
    }

    @RequestMapping(value = "config", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    //@Secured("ROLE_LTI")
    public LtiProxyConfig saveConfig(@RequestBody LtiProxyConfig updates, HttpServletRequest req, Principal principal) {
//        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)principal;
//        final LTIUserCredentials ltiOauthPrincipal = (LTIUserCredentials)token.getCredentials();
        LtiProxyConfig config = this.lookupLtiLaunchConfig("admin", "mercury");//ltiOauthPrincipal.getUserId(), ltiOauthPrincipal.getContextId());
        if (config == null) {
            config = new LtiProxyConfig("admin", "mercury");//ltiOauthPrincipal.getUserId(), ltiOauthPrincipal.getContextId());
        }
        config.setConsumerKey(updates.getConsumerKey());
        config.setConsumerSecret(updates.getConsumerSecret());
        config.setUrl(updates.getUrl());
        return this.saveLtiLaunchConfig(config);
    }

    private LtiProxyConfig lookupLtiLaunchConfig(final String userId, final String contextId) {
        log.debug("Looking up LTI launch config for [{}, {}].", userId, contextId);
        final LtiProxyConfig result = this.launchConfigMap.get(userId + ":" + contextId);
        log.debug("Lookup returning: {}", result);
        return result;
    }

    private LtiProxyConfig saveLtiLaunchConfig(final LtiProxyConfig config) {
        log.debug("Saving LTI launch config: {}", config);
        final String key = config.getUserId() + ":" + config.getContextId();
        this.launchConfigMap.put(key, config);
        return this.launchConfigMap.get(key);
    }

    private LaunchRequest createLaunchRequest(final LtiProxyConfig config) {
        final LaunchRequest result = new LaunchRequest(
            "basic-lti-launch-request", // ltiRequest.getLtiMessageType(),
            "LTI-1p0", //ltiRequest.getLtiVersion(),
            null, // ltiRequest.getLtiLinkId(),
            config.getContextId(), // ltiRequest.getLtiContextId(),
            null, // ltiRequest.getLtiPresTarget(),
            null, // ltiRequest.getLtiPresWidth(),
            null, // ltiRequest.getLtiPresHeight(),
            null, // ltiRequest.getLtiPresReturnUrl(),
            config.getUrl(), // ltiRequest.getLtiUserId(),
            "admin, instructor, learner", //ltiRequest.getLtiUserRoles().toString(),
            null, // context type ??? ltiRequest.getContext().get) {
            null, // ltiRequest.getLtiPresLocale(),
            null, // launch_presentation_css_url ??? ltiRequest.getLtiPr
            null, // role_scope_mentorltiRequest.getLti
            null, // String user_image,
            null, // Map<String, String> custom,
            null, // Map<String, String> ext,
            null, // String resource_link_title,
            null, // String resource_link_description,
            null, // String lis_person_name_given,
            null, // String lis_person_name_family,
            null, // String lis_person_name_full,
            null, // String lis_person_contact_email_primary,
            null, // String lis_outcome_service_url,
            null, // String lis_result_sourcedid,
            null, // String context_title,
            null, // String context_label,
            null, // String tool_consumer_info_product_family_code,
            null, // String tool_consumer_info_version,
            null, // String tool_consumer_instance_guid,
            null, // String tool_consumer_instance_name,
            null, // String tool_consumer_instance_description,
            null, // String tool_consumer_instance_url,
            null, // String tool_consumer_instance_contact_email,
            config.getConsumerKey(), //String oauth_consumer_key,
            "HMAC-SHA1", // String oauth_signature_method,
            Long.toString((System.currentTimeMillis() / 1000)), // String oauth_timestamp,
            OAuthUtil.generateNonce(), // String oauth_nonce,
            "1.0", // String oauth_version,
            null, // String oauth_signature,
            null); // String oauth_callback);
        return result;
    }

    private String calculateOauthSignature(final LtiProxyConfig config, final LaunchRequest request) {
        String signature = null;
        try {
            final SortedMap<String,String> alphaSortedMap = request.toSortedMap();
            signature = this.signer.sign(
                    config.getConsumerSecret(),
                    OAuthUtil.mapToJava(alphaSortedMap.get(OAuthUtil.SIGNATURE_METHOD_PARAM)),
                    "POST",
                    config.getUrl(),
                    alphaSortedMap);
        } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("Unable to construct OAuth signature");
        }
        log.debug("CALCULATED SIGNATURE: {}", signature);
        return signature;
    }

}