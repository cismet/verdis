/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleBemerkung {

    //~ Instance fields --------------------------------------------------------

    private Date erstellt_am;
    private String erstellt_von;
    private String bemerkung;
    private Integer verfaellt_tage;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @JsonIgnore
    public Date getVerfallsDatum() {
        if (verfaellt_tage == null) {
            return null;
        } else {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(getErstellt_am());
            cal.add(Calendar.DATE, getVerfaellt_tage());
            return cal.getTime();
        }
    }
}
