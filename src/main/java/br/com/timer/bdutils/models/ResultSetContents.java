package br.com.timer.bdutils.models;

import br.com.infernalia.flat.Flat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultSetContents {

    private Flat<Params> params;
    private boolean containsSearch;

}
