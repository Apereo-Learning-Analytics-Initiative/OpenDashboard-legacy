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

import javax.persistence.*;

@Entity
@Table(name = "lms_plugins")
public class LmsPluginsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "plugin_id", nullable = false, insertable = true, updatable = true)
    private long pluginId;
    @Basic
    @Column(name = "plugin_path", nullable = false, insertable = true, updatable = true, length = 255)
    private String pluginPath;
    @Basic
    @Column(name = "plugin_version", nullable = false, insertable = true, updatable = true)
    private long pluginVersion;
    @Basic
    @Column(name = "title", nullable = true, insertable = true, updatable = true, length = 4096)
    private String title;
    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
    private String json;

    public long getPluginId() {
        return pluginId;
    }

    public void setPluginId(long pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public long getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(long version) {
        this.pluginVersion = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LmsPluginsEntity that = (LmsPluginsEntity) o;

        if (pluginId != that.pluginId) return false;
        if (pluginVersion != that.pluginVersion) return false;
        if (pluginPath != null ? !pluginPath.equals(that.pluginPath) : that.pluginPath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) pluginId;
        result = 31 * result + (pluginPath != null ? pluginPath.hashCode() : 0);
        result = 31 * result + (int) (pluginVersion ^ (pluginVersion >>> 32));
        return result;
    }
}
