package com.doo.xenchantment.interfaces;

import com.google.gson.JsonObject;

public interface WithOptions {

    JsonObject getOptions();

    void loadOptions(JsonObject json);
}
