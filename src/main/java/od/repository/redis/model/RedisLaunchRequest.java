/**
 *
 */
package od.repository.redis.model;

import java.util.Map;
import java.util.TreeMap;

import lti.LtiMessage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ggilbert
 *
 */
public class RedisLaunchRequest extends LtiMessage {

    private String resource_link_id;

    /* Recommended Parameters */
    private String context_id;
    private String launch_presentation_document_target;
    private String launch_presentation_width;
    private String launch_presentation_height;
    private String launch_presentation_return_url;
    private String user_id;
    private String roles;

    /* Optional Parameters */
    private String context_type;
    private String launch_presentation_locale;
    private String launch_presentation_css_url;
    private String role_scope_mentor;
    private String user_image;

    /* Deprecated Parameters */
    private String resource_link_title;
    private String resource_link_description;
    private String lis_person_name_given;
    private String lis_person_name_family;
    private String lis_person_name_full;
    private String lis_person_contact_email_primary;
    private String lis_outcome_service_url;
    private String lis_result_sourcedid;
    private String context_title;
    private String context_label;
    private String tool_consumer_info_product_family_code;
    private String tool_consumer_info_version;
    private String tool_consumer_instance_guid;
    private String tool_consumer_instance_name;
    private String tool_consumer_instance_description;
    private String tool_consumer_instance_url;
    private String tool_consumer_instance_contact_email;

    public RedisLaunchRequest() {}

    public RedisLaunchRequest(Map<String, String []> paramMap) {
        Map<String,String> flattenedParams = new TreeMap<String, String>();

        for (String key : paramMap.keySet()) {
            String [] values = paramMap.get(key);
            String value = null;
            if (values != null && values.length > 0) {
                for (String v : values) {
                    if (value == null) {
                        value = v;
                    }
                    else {
                        value = value.concat(",");
                        value = value.concat(v);
                    }
                }
            }
            flattenedParams.put(key, value);
        }
    }

    @JsonCreator
    public RedisLaunchRequest(
            @JsonProperty("lti_message_type") String lti_message_type,
            @JsonProperty("lti_version") String lti_version,
            @JsonProperty("resource_link_id") String resource_link_id,
            @JsonProperty("context_id") String context_id,
            @JsonProperty("launch_presentation_document_target") String launch_presentation_document_target,
            @JsonProperty("launch_presentation_width") String launch_presentation_width,
            @JsonProperty("launch_presentation_height") String launch_presentation_height,
            @JsonProperty("launch_presentation_return_url") String launch_presentation_return_url,
            @JsonProperty("user_id") String user_id,
            @JsonProperty("roles") String roles,
            @JsonProperty("context_type") String context_type,
            @JsonProperty("launch_presentation_locale") String launch_presentation_locale,
            @JsonProperty("launch_presentation_css_url") String launch_presentation_css_url,
            @JsonProperty("role_scope_mentor") String role_scope_mentor,
            @JsonProperty("user_image") String user_image,
            @JsonProperty("resource_link_title") String resource_link_title,
            @JsonProperty("resource_link_description") String resource_link_description,
            @JsonProperty("lis_person_name_given") String lis_person_name_given,
            @JsonProperty("lis_person_name_family") String lis_person_name_family,
            @JsonProperty("lis_person_name_full") String lis_person_name_full,
            @JsonProperty("lis_person_contact_email_primary") String lis_person_contact_email_primary,
            @JsonProperty("lis_outcome_service_url") String lis_outcome_service_url,
            @JsonProperty("lis_result_sourcedid") String lis_result_sourcedid,
            @JsonProperty("context_title") String context_title,
            @JsonProperty("context_label") String context_label,
            @JsonProperty("tool_consumer_info_product_family_code") String tool_consumer_info_product_family_code,
            @JsonProperty("tool_consumer_info_version") String tool_consumer_info_version,
            @JsonProperty("tool_consumer_instance_guid") String tool_consumer_instance_guid,
            @JsonProperty("tool_consumer_instance_name") String tool_consumer_instance_name,
            @JsonProperty("tool_consumer_instance_description") String tool_consumer_instance_description,
            @JsonProperty("tool_consumer_instance_url") String tool_consumer_instance_url,
            @JsonProperty("tool_consumer_instance_contact_email") String tool_consumer_instance_contact_email,
            @JsonProperty("oauth_consumer_key") String oauth_consumer_key,
            @JsonProperty("oauth_signature_method") String oauth_signature_method,
            @JsonProperty("oauth_timestamp") String oauth_timestamp,
            @JsonProperty("oauth_nonce") String oauth_nonce,
            @JsonProperty("oauth_version") String oauth_version,
            @JsonProperty("oauth_signature") String oauth_signature,
            @JsonProperty("oauth_callback") String oauth_callback) {

        super(lti_message_type, lti_version,
                oauth_consumer_key, oauth_signature_method,
                oauth_timestamp, oauth_nonce, oauth_version,
                oauth_signature, oauth_callback);

        this.resource_link_id = resource_link_id;
        this.context_id = context_id;
        this.launch_presentation_document_target = launch_presentation_document_target;
        this.launch_presentation_width = launch_presentation_width;
        this.launch_presentation_height = launch_presentation_height;
        this.launch_presentation_return_url = launch_presentation_return_url;
        this.user_id = user_id;
        this.roles = roles;
        this.context_type = context_type;
        this.launch_presentation_locale = launch_presentation_locale;
        this.launch_presentation_css_url = launch_presentation_css_url;
        this.role_scope_mentor = role_scope_mentor;
        this.user_image = user_image;
        this.resource_link_title = resource_link_title;
        this.resource_link_description = resource_link_description;
        this.lis_person_name_given = lis_person_name_given;
        this.lis_person_name_family = lis_person_name_family;
        this.lis_person_name_full = lis_person_name_full;
        this.lis_person_contact_email_primary = lis_person_contact_email_primary;
        this.lis_outcome_service_url = lis_outcome_service_url;
        this.lis_result_sourcedid = lis_result_sourcedid;
        this.context_title = context_title;
        this.context_label = context_label;
        this.tool_consumer_info_product_family_code = tool_consumer_info_product_family_code;
        this.tool_consumer_info_version = tool_consumer_info_version;
        this.tool_consumer_instance_guid = tool_consumer_instance_guid;
        this.tool_consumer_instance_name = tool_consumer_instance_name;
        this.tool_consumer_instance_description = tool_consumer_instance_description;
        this.tool_consumer_instance_url = tool_consumer_instance_url;
        this.tool_consumer_instance_contact_email = tool_consumer_instance_contact_email;
    }

    @Override
    public String toString() {
        return "LaunchRequest [resource_link_id=" + resource_link_id
                + ", context_id=" + context_id
                + ", launch_presentation_document_target="
                + launch_presentation_document_target
                + ", launch_presentation_width=" + launch_presentation_width
                + ", launch_presentation_height=" + launch_presentation_height
                + ", launch_presentation_return_url="
                + launch_presentation_return_url + ", user_id=" + user_id
                + ", roles=" + roles + ", context_type=" + context_type
                + ", launch_presentation_locale=" + launch_presentation_locale
                + ", launch_presentation_css_url="
                + launch_presentation_css_url + ", role_scope_mentor="
                + role_scope_mentor + ", user_image=" + user_image
                + ", oauth_consumer_key=" + oauth_consumer_key
                + ", oauth_signature_method=" + oauth_signature_method
                + ", oauth_timestamp=" + oauth_timestamp + ", oauth_nonce="
                + oauth_nonce + ", oauth_version=" + oauth_version
                + ", oauth_signature=" + oauth_signature + ", oauth_callback="
                + oauth_callback + ", resource_link_title="
                + resource_link_title + ", resource_link_description="
                + resource_link_description + ", lis_person_name_given="
                + lis_person_name_given + ", lis_person_name_family="
                + lis_person_name_family + ", lis_person_name_full="
                + lis_person_name_full + ", lis_person_contact_email_primary="
                + lis_person_contact_email_primary
                + ", lis_outcome_service_url=" + lis_outcome_service_url
                + ", lis_result_sourcedid=" + lis_result_sourcedid
                + ", context_title=" + context_title + ", context_label="
                + context_label + ", tool_consumer_info_product_family_code="
                + tool_consumer_info_product_family_code
                + ", tool_consumer_info_version=" + tool_consumer_info_version
                + ", tool_consumer_instance_guid="
                + tool_consumer_instance_guid
                + ", tool_consumer_instance_name="
                + tool_consumer_instance_name
                + ", tool_consumer_instance_description="
                + tool_consumer_instance_description
                + ", tool_consumer_instance_url=" + tool_consumer_instance_url
                + ", tool_consumer_instance_contact_email="
                + tool_consumer_instance_contact_email + ", lti_message_type="
                + lti_message_type + ", lti_version=" + lti_version + "]";
    }

    public String getResource_link_id() {
        return resource_link_id;
    }

    public String getContext_id() {
        return context_id;
    }

    public String getLaunch_presentation_document_target() {
        return launch_presentation_document_target;
    }

    public String getLaunch_presentation_width() {
        return launch_presentation_width;
    }

    public String getLaunch_presentation_height() {
        return launch_presentation_height;
    }

    public String getLaunch_presentation_return_url() {
        return launch_presentation_return_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getRoles() {
        return roles;
    }

    public String getContext_type() {
        return context_type;
    }

    public String getLaunch_presentation_locale() {
        return launch_presentation_locale;
    }

    public String getLaunch_presentation_css_url() {
        return launch_presentation_css_url;
    }

    public String getRole_scope_mentor() {
        return role_scope_mentor;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getResource_link_title() {
        return resource_link_title;
    }

    public void setResource_link_title(String resource_link_title) {
        this.resource_link_title = resource_link_title;
    }

    public String getResource_link_description() {
        return resource_link_description;
    }

    public String getLis_person_name_given() {
        return lis_person_name_given;
    }

    public String getLis_person_name_family() {
        return lis_person_name_family;
    }

    public void setLis_person_name_family(String lis_person_name_family) {
        this.lis_person_name_family = lis_person_name_family;
    }

    public String getLis_person_name_full() {
        return lis_person_name_full;
    }

    public String getLis_person_contact_email_primary() {
        return lis_person_contact_email_primary;
    }

    public String getLis_outcome_service_url() {
        return lis_outcome_service_url;
    }

    public String getLis_result_sourcedid() {
        return lis_result_sourcedid;
    }

    public String getContext_title() {
        return context_title;
    }

    public String getContext_label() {
        return context_label;
    }

    public String getTool_consumer_info_product_family_code() {
        return tool_consumer_info_product_family_code;
    }

    public String getTool_consumer_info_version() {
        return tool_consumer_info_version;
    }

    public String getTool_consumer_instance_guid() {
        return tool_consumer_instance_guid;
    }

    public String getTool_consumer_instance_name() {
        return tool_consumer_instance_name;
    }

    public String getTool_consumer_instance_description() {
        return tool_consumer_instance_description;
    }

    public String getTool_consumer_instance_url() {
        return tool_consumer_instance_url;
    }

    public String getTool_consumer_instance_contact_email() {
        return tool_consumer_instance_contact_email;
    }
}
