package manage.model;

import m.common.model.FieldMeta;
import m.common.model.Model;
import m.common.model.type.FieldType;

public class StatusModel extends Model {

	@FieldMeta(name="status",type=FieldType.STRING,length=1,description="状态 0正常 9禁用")
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
