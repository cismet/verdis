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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.verdis.commons.constants.FrontPropertyConstants;
import de.cismet.verdis.commons.constants.FrontinfoPropertyConstants;
import de.cismet.verdis.commons.constants.StrassenreinigungPropertyConstants;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FrontFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Instance fields --------------------------------------------------------

    CustomFixedWidthStroke stroke = new CustomFixedWidthStroke(10f, CismapBroker.getInstance().getMappingComponent());
    boolean sr = false;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
        final CidsBean satzung_strassenreinigung = (CidsBean)cidsBean.getProperty(
                FrontPropertyConstants.PROP__FRONTINFO
                        + "."
                        + FrontinfoPropertyConstants.PROP__LAGE_SR);
        final String key;
        if (satzung_strassenreinigung == null) {
            key = (String)cidsBean.getProperty(FrontPropertyConstants.PROP__FRONTINFO + "."
                            + FrontinfoPropertyConstants.PROP__SR_KLASSE_OR + "."
                            + StrassenreinigungPropertyConstants.PROP__KEY);
        } else {
            key = (String)satzung_strassenreinigung.getProperty("sr_klasse.key");
        }

        sr = key != null;
    }

    @Override
    public Paint getLinePaint() {
        if (cidsBean != null) {
            assign();
        }
        if (sr) {
            return Color.ORANGE;
        } else {
            return Color.GREEN.darker();
        }
    }

    @Override
    public Stroke getLineStyle() {
        return stroke;
    }
}
