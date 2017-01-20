/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.verdis.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultiBemerkung {

    //~ Instance fields --------------------------------------------------------

    private List<SingleBemerkung> bemerkungen;
}
