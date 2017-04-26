package ceshi.zxf.com.audio;

import java.util.List;

public class AttachmentModel {

	private long UniqueID=0;
	public String attachmentName;
	public String attachmentPath;
	public String type;//0：录音 1：视频 2:图片 3：其它 
	public String truePath;
	public String fileSize;
	public List<AttachmentModel> childAttachmentModel;

	public AttachmentModel(String urlName, String urlPath,String type,String truePath) {
		super();
		this.attachmentName = urlName;
		this.attachmentPath = urlPath;
		this.type = type;
		this.truePath = truePath;
	}

	public AttachmentModel(String attachmentName, String attachmentPath,
			String type, String truePath, String fileSize) {
		super();
		this.attachmentName = attachmentName;
		this.attachmentPath = attachmentPath;
		this.type = type;
		this.truePath = truePath;
		this.fileSize = fileSize;
	}

	public AttachmentModel() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((truePath == null) ? 0 : truePath.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((attachmentName == null) ? 0 : attachmentName.hashCode());
		result = prime * result + ((attachmentPath == null) ? 0 : attachmentPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttachmentModel other = (AttachmentModel) obj;
		if (truePath == null) {
			if (other.truePath != null)
				return false;
		} else if (!truePath.equals(other.truePath))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (attachmentName == null) {
			if (other.attachmentName != null)
				return false;
		} else if (!attachmentName.equals(other.attachmentName))
			return false;
		if (attachmentPath == null) {
			if (other.attachmentPath != null)
				return false;
		} else if (!attachmentPath.equals(other.attachmentPath))
			return false;
		return true;
	}

	public long getUniqueID() {
		return UniqueID;
	}

	public void setUniqueID(long uniqueID) {
		UniqueID = uniqueID;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTruePath() {
		return truePath;
	}

	public void setTruePath(String truePath) {
		this.truePath = truePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public List<AttachmentModel> getChildAttachmentModel() {
		return childAttachmentModel;
	}

	public void setChildAttachmentModel(List<AttachmentModel> childAttachmentModel) {
		this.childAttachmentModel = childAttachmentModel;
	}

	
}
