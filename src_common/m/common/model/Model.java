package m.common.model;

import java.io.Serializable;

/**
 * 模型基础类. 所有表的主键都是oid
 * @author Administrator
 *
 */
public class Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@KeyFieldMeta(name="oid")
	private String oid;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
}
