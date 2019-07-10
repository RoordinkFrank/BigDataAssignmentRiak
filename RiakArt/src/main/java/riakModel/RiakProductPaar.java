package riakModel;


public class RiakProductPaar {
	public String firstProductKey;
	public String otherProductKey;
	public int samenGekocht;
	
	public RiakProductPaar(String firstProductKey, String otherProductKey) {
		super();
		this.firstProductKey = firstProductKey;
		this.otherProductKey = otherProductKey;
		samenGekocht = 0;
	}

	@Override
	public String toString() {
		return "RiakProductPaar [firstProductKey=" + firstProductKey
				+ ", otherProductKey=" + otherProductKey + ", samenGekocht="
				+ samenGekocht + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstProductKey == null) ? 0 : firstProductKey.hashCode());
		result = prime * result
				+ ((otherProductKey == null) ? 0 : otherProductKey.hashCode());
		result = prime * result + samenGekocht;
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
		RiakProductPaar other = (RiakProductPaar) obj;
		if (firstProductKey == null) {
			if (other.firstProductKey != null)
				return false;
		} else if (!firstProductKey.equals(other.firstProductKey))
			return false;
		if (otherProductKey == null) {
			if (other.otherProductKey != null)
				return false;
		} else if (!otherProductKey.equals(other.otherProductKey))
			return false;
		if (samenGekocht != other.samenGekocht)
			return false;
		return true;
	}

	
}
