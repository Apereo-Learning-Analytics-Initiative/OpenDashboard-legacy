/**
 * 
 */
package od.providers.api;

import java.util.List;

import org.springframework.data.domain.Sort;

/**
 * @author ggilbert
 *
 */
public class PageWrapper<T> {
  private List<T> content;

  private Boolean last;
  private Boolean first;
  private Integer totalPages;
  private Integer totalElements;
  private Integer size;
  private Integer number;
  private Integer numberOfElements;
  private Sort sort;
  
  public List<T> getContent() {
    return content;
  }
  public void setContent(List<T> content) {
    this.content = content;
  }
  public Boolean getLast() {
    return last;
  }
  public void setLast(Boolean last) {
    this.last = last;
  }
  public Boolean getFirst() {
    return first;
  }
  public void setFirst(Boolean first) {
    this.first = first;
  }
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
  public Integer getNumberOfElements() {
    return numberOfElements;
  }
  public void setNumberOfElements(Integer numberOfElements) {
    this.numberOfElements = numberOfElements;
  }
  public Sort getSort() {
    return sort;
  }
  public void setSort(Sort sort) {
    this.sort = sort;
  }
  @Override
  public String toString() {
    return "PageWrapper [content=" + content + ", last=" + last + ", first=" + first + ", totalPages=" + totalPages + ", totalElements="
        + totalElements + ", size=" + size + ", number=" + number + ", numberOfElements=" + numberOfElements + ", sort=" + sort + "]";
  }
 }