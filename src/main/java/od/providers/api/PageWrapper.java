/*******************************************************************************
 * Copyright 2015 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package od.providers.api;

import java.util.List;

/**
 * @author ggilbert
 *
 */
public class PageWrapper<T> {
  
  @Override
  public String toString() {
    return "PageWrapper [content=" + content + ", page=" + page + "]";
  }
  
  public class PageInfo {
    @Override
    public String toString() {
      return "PageInfo [totalPages=" + totalPages + ", totalElements=" + totalElements + ", size=" + size + ", number=" + number + "]";
    }
    
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private Integer number;
    
    public Integer getTotalPages() {
      return totalPages;
    }
    public void setTotalPages(Integer totalPages) {
      this.totalPages = totalPages;
    }
    public Integer getTotalElements() {
      return totalElements;
    }
    public void setTotalElements(Integer totalElements) {
      this.totalElements = totalElements;
    }
    public Integer getSize() {
      return size;
    }
    public void setSize(Integer size) {
      this.size = size;
    }
    public Integer getNumber() {
      return number;
    }
    public void setNumber(Integer number) {
      this.number = number;
    }

  }
  
  private List<T> content;
  private PageInfo page;
  
  public List<T> getContent() {
    return content;
  }
  public void setContent(List<T> content) {
    this.content = content;
  }
  public PageInfo getPage() {
    return page;
  }
  public void setPage(PageInfo page) {
    this.page = page;
  }
 }