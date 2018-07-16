/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 srichter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.featurerenderer.verdis_grundis;

import java.awt.Paint;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class KassenzeichenFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Instance fields --------------------------------------------------------

    javax.swing.ImageIcon icoKassenzeichen = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/verdis/res/images/titlebars/kassenzeichen.png"));

    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
    }

    @Override
    public Paint getFillingStyle() {
        return new java.awt.Color(0, 100, 0, 90);
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        final FeatureAnnotationSymbol fas = new FeatureAnnotationSymbol(icoKassenzeichen.getImage());
        fas.setSweetSpotX(0.5d);
        fas.setSweetSpotY(0.5d);
        return fas;
    }
}
