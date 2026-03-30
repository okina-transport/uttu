package no.entur.uttu.model.job;

public class Link implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public static final String GET_METHOD = "get";
	public static final String LOCATION_REL = "location";

	private String type;
	private String rel;
	private String method;
	private String href;

	public Link() {
	}

	public Link(String type, String rel) {
		this.type = type;
		this.rel = rel;
	}

	public String getRel() {
		return rel;
	}

	public String getHref() {
		return href;
	}

	public String getType() {
		return type;
	}

	public String getMethod() {
		return method;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}