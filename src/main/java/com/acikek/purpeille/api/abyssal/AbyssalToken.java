package com.acikek.purpeille.api.abyssal;

import com.acikek.purpeille.warpath.component.Revelation;

public interface AbyssalToken {

    Revelation getRevelation();

    void setRevelation(Revelation revelation);

    default boolean isAbyssalToken() {
        return getRevelation() != null;
    }
}
