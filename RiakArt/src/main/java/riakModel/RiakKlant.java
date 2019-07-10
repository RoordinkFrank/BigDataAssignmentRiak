package riakModel;

import com.basho.riak.client.api.commands.kv.UpdateValue;

public class RiakKlant {
	public String key;
	
	public RiakKlant(){
		
	}
	
	public RiakKlant(String key) {
		super();
		this.key = key;
	}
	
	public static class KlantUpdate extends UpdateValue.Update<RiakKlant> {
        private final RiakKlant update;
        public KlantUpdate(RiakKlant update){
            this.update = update;
        }

        @Override
        public RiakKlant apply(RiakKlant t) {
            if(t == null) {
                t = new RiakKlant();
            }

            t.key = update.key;
            return t;
        }
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RiakKlant other = (RiakKlant) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RiakKlant [key=" + key + "]";
	}

}
