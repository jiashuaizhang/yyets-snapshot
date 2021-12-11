package com.zhangjiashuai.yyetshistory.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("resource")
public class ResourceDO {

  @TableId
  private Long id;
  private String url;
  private String name;
  private Long expire;
  private String expireCst;
  private String data;

}
