package common.actions;

import java.io.Serializable;

public class Response implements Serializable {
    private String response;
    private ResponseCode isGoodResponse;
    private String[] responseBodyArgs;
    private Object object;

    public Response(ResponseCode responseCode, String response, String[] responseBodyArgs) {
        this.isGoodResponse = responseCode;
        this.response = response;
        this.responseBodyArgs = responseBodyArgs;
    }
    public Response(Object object, ResponseCode responseCode){
        this.object = object;
        this.isGoodResponse = responseCode;
    }


    /**
     * @return Response
     */
    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "UserResponse[" + response+ "]";
    }

    public ResponseCode getIsGoodResponse() {
        return isGoodResponse;
    }
    public Object getResponseObject(){
        return this.object;
    }
}
