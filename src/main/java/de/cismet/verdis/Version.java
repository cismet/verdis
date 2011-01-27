/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * Version.java
 *
 * Created on 18. M\u00E4rz 2005, 13:00
 *
 *
 * 1.8
 * Umstellung auf SplitPolygonListener
 * 1.9
 * MultiPointSplitting
 * 1.11
 * Bug: [Wird ein Polygon entfernt und direkt ein neues zugeordnet, wird die gesamte Fl\u00E4che gel\u00F6scht] behoben
 * 1.13
 * Zeitverschiebung eingetragen
 * 1.14
 * Bugs entfernt (Handle sichtbar wenn RW Wechsel nicht im Select Mode)
 * 1.15
 * kein erneutes log4j konfigurieren wenn plugin
 * 1.16
 * Versionsbug
 * 1.17
 * Versionsbug
 * 1.19
 * Bugfix: Split und MoveHandles
 * 1.20
 * Bugfix RefreshBug
 * 1.21
 * Neues Kassenzeichen
 * 1.22
 * 1.23
 * Sperrmassakerbug bei neuem Kassenzeichen
 * 1.24
 * Richtige Reihenefolge der Stra\u00DFenfl\u00E4chen
 * 1.25
 * BugFix Connections
 * 1.26
 * BugFix Neue Stra\u00DFenfl\u00E4che hatte keinen Namen wenn andere Fl\u00E4chen vorher
 * 1.27
 * BugFix: Ausschneiden hat die Eintr\u00E4ge in cs_all_attr_mapping nicht ver\u00E4ndert
 * 1.28
 * Webstarttest
 * 1.29
 * RefreshEnum
 * 1.30
 * Sicherheitsabfrage bei cut und paste
 * 1.31
 * Kanalanschlussinformationen
 * 1.34
 * Nach Umstellungxxx
 */
package de.cismet.verdis;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class Version {

    //~ Static fields/initializers ---------------------------------------------

    private static final String VERSION =
        "verdis.jar Version:2 ($Date: 2009-08-31 08:29:42 $(+2) jarVersion:$Revision: 1.1.1.1 $) ";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println(getVersion());
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getVersion() {
        return VERSION;
    }
}
