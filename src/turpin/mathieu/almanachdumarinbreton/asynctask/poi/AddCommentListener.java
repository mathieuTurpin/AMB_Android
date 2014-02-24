package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;

public interface AddCommentListener{
	void addCommentService(ServiceDTO poi);
	void addCommentPoi(PoiDTO poi);
}
