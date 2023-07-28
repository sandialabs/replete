package replete.text.tokens;

import java.io.Serializable;

public class LocationAwareToken<T extends Serializable> {
    public T token;
    public int start;
    public int endNonIncl;

    public LocationAwareToken(T token, int start, int endNonIncl) {
        this.token = token;
        this.start = start;
        this.endNonIncl = endNonIncl;
    }

    public T getToken() {
        return token;
    }
    public int getStart() {
        return start;
    }
    public int getEndNonIncl() {
        return endNonIncl;
    }
}
