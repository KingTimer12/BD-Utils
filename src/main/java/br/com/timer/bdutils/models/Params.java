package br.com.timer.bdutils.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Params {

    private String key;
    private Object value;

    public String toStringEncoded() {
        return key + "=?";
    }

}
