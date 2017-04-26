
package com.fedexday.trump.trump;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrumpResponse {

    @SerializedName("ResponseText")
    @Expose
    private String responseText;

    public TrumpResponse(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

}
