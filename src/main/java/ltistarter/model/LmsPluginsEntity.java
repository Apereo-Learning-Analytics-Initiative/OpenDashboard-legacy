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
    private int pluginId;
    private String pluginPath;
    private long pluginVersion;
    private String title;
    private String json;

    @Id
    @Column(name = "plugin_id", nullable = false, insertable = true, updatable = true)
    public int getPluginId() {
        return pluginId;
    }

    public void setPluginId(int pluginId) {
        this.pluginId = pluginId;
    }

    @Basic
    @Column(name = "plugin_path", nullable = false, insertable = true, updatable = true, length = 255)
    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    @Basic
    @Column(name = "plugin_version", nullable = false, insertable = true, updatable = true)
    public long getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(long version) {
        this.pluginVersion = version;
    }

    @Basic
    @Column(name = "title", nullable = true, insertable = true, updatable = true, length = 2048)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "json", nullable = true, insertable = true, updatable = true, length = 65535)
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
        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (pluginPath != null ? !pluginPath.equals(that.pluginPath) : that.pluginPath != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pluginId;
        result = 31 * result + (pluginPath != null ? pluginPath.hashCode() : 0);
        result = 31 * result + (int) (pluginVersion ^ (pluginVersion >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }
}
