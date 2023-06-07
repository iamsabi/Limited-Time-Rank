package xyz.dwbrss.ltr.json;

import com.google.gson.annotations.SerializedName;

public class ConfigJson {
    @SerializedName("default_group")
    public String DEFAULT_GROUP;
    @SerializedName("version")
    public String VERSION;
    @SerializedName("key_length")
    public int LENGTH;
    @SerializedName("key_prefix")
    public String PREFIX;
    @SerializedName("key_suffix")
    public String SUFFIX;
}
