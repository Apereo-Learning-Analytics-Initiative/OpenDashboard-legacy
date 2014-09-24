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
package ltistarter.model;

import java.util.Map;

public class LaunchForm {
	private String launchUrl;
	private boolean debug;
	private Map<String, String> parameters;
	private String signature;
	
	public LaunchForm(String launchUrl, boolean debug,
			Map<String, String> parameters, String signature) {
		this.launchUrl = launchUrl;
		this.debug = debug;
		this.parameters = parameters;
		this.signature = signature;
	}

	public String getLaunchUrl() {
		return launchUrl;
	}

	public boolean isDebug() {
		return debug;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getSignature() {
		return signature;
	}
}
