package alvin.study.jdbc.mptt.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MPTT {
    private Long id;
    private String name;
    private Integer lft;
    private Integer rht;
}
