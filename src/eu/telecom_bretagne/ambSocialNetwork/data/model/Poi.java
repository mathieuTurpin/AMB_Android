package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.io.Serializable;

public class Poi implements Serializable
{
  private static final long serialVersionUID = 1L;

  private Integer id;

  //private Object type;
  private String type;

  private Point point;

  public Poi() {}

  public Integer getId()    { return this.id;    }
  //public Object  getType()  { return this.type;  }
  public String  getType()  { return this.type;  }
  public Point   getPoint() { return this.point; }

  public void setId(Integer id)     { this.id    = id;    }
  //public void setType(Object type)  { this.type  = type;  }
  public void setType(String type)  { this.type  = type;  }
  public void setPoint(Point point) { this.point = point; }

}