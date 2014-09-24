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
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import ltistarter.model.LaunchForm;
import ltistarter.model.LaunchRequest;
import ltistarter.oauth.MyOAuthAuthenticationHandler.NamedOAuthPrincipal;
import ltistarter.oauth.OAuthMessageSigner;
import ltistarter.oauth.OAuthUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for displaying OpenLRS view.
 */
@Controller
@RequestMapping("/openlrs")
public class OpenLRSController extends BaseController {

    @Value("${openlrs.lti.user.id}")
    private String openLrsLtiUserId;
    @Value("${openlrs.lti.context.id}")
    private String openLrsLtiContextId;
    @Value("${openlrs.lti.url}")
    private String openLrsLtiUrl;
    @Value("${openlrs.lti.consumer.key}")
    private String openLrsLtiConsumerKey;
    @Value("${openlrs.lti.consumer.secret}")
    private String openLrsLtiConsumerSecret;

    private OAuthMessageSigner signer = new OAuthMessageSigner();

    @RequestMapping({"", "/launch"})
    //@Secured("ROLE_LTI")
    public String openlrs(HttpServletRequest req, Principal principal, Model model) {
        final NamedOAuthPrincipal namedOauthPrincipal = (NamedOAuthPrincipal)principal;
        final LaunchRequest launchRequest = this.createLaunchRequest(namedOauthPrincipal);
        final String signature = this.calculateOauthSignature(launchRequest);
        final LaunchForm launchForm = new LaunchForm(this.openLrsLtiUrl, false, launchRequest.toSortedMap(), signature);
        model.addAttribute("launchForm", launchForm);
        return "ltilaunch"; // name of the template
    }

    private LaunchRequest createLaunchRequest(final NamedOAuthPrincipal principal) {
        final LaunchRequest result = new LaunchRequest(
            "basic-lti-launch-request", // ltiRequest.getLtiMessageType(),
            "LTI-1p0", //ltiRequest.getLtiVersion(),
            null, // ltiRequest.getLtiLinkId(),
            this.openLrsLtiContextId, // ltiRequest.getLtiContextId(),
            null, // ltiRequest.getLtiPresTarget(),
            null, // ltiRequest.getLtiPresWidth(),
            null, // ltiRequest.getLtiPresHeight(),
            null, // ltiRequest.getLtiPresReturnUrl(),
            this.openLrsLtiUserId, // ltiRequest.getLtiUserId(),
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
            this.openLrsLtiConsumerKey, //String oauth_consumer_key,
            "HMAC-SHA1", // String oauth_signature_method,
            Long.toString((System.currentTimeMillis() / 1000)), // String oauth_timestamp,
            OAuthUtil.generateNonce(), // String oauth_nonce,
            "1.0", // String oauth_version,
            null, // String oauth_signature,
            null); // String oauth_callback);
        return result;
    }

    private String calculateOauthSignature(final LaunchRequest request) {
        String signature = null;
        try {
            final SortedMap<String,String> alphaSortedMap = request.toSortedMap();
            signature = this.signer.sign(
                    this.openLrsLtiConsumerSecret,
                    OAuthUtil.mapToJava(alphaSortedMap.get(OAuthUtil.SIGNATURE_METHOD_PARAM)),
                    "POST",
                    this.openLrsLtiUrl,
                    alphaSortedMap);
        } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException("Unable to construct OAuth signature");
        }
        log.debug("CALCULATED SIGNATURE: {}", signature);
        return signature;
    }

}