/*
 *  A22 Car Park Info
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-05-17  1.0 - chris@1006.org
 */

package it.bz.noi.a22.parking;

public class A22CarParkInfo {

                                        // rif.: Softech - 2017-002 Autostrada del Brennero S.p.A.
                                        // Manutenzione ordinaria e straordinaria sistema di supervisione CAU - anno 2017 - N. doc: 2017-0046 Revisione: 9

    private Long id;                    // Identificativo univoco del parcheggio
    private String descrizione;         // Nome del parcheggio
    private String autostrada;          // Sigla dell’autostrada di appartenenza del parcheggio
    private Long iddirezione;           // Identificativo della carreggiata in cui è localizzato il parcheggio Vedi tabella DIREZIONI 5.9.1
    private Long metro;                 // Progressiva chilometrica del parcheggio
    private Double latitudine;          // Coordinate latitudinali del parcheggio, in formato WGS84
    private Double longitudine;         // Coordinate longitudinali del parcheggio, in formato WGS84

    @Override
    public String toString() {
        return "A22CarParkInfo{" +
                "id=" + id +
                ", descrizione='" + descrizione + '\'' +
                ", autostrada='" + autostrada + '\'' +
                ", iddirezione=" + iddirezione +
                ", metro=" + metro +
                ", latitudine=" + latitudine +
                ", longitudine=" + longitudine +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getAutostrada() {
        return autostrada;
    }

    public void setAutostrada(String autostrada) {
        this.autostrada = autostrada;
    }

    public Long getIddirezione() {
        return iddirezione;
    }

    public void setIddirezione(Long iddirezione) {
        this.iddirezione = iddirezione;
    }

    public Long getMetro() {
        return metro;
    }

    public void setMetro(Long metro) {
        this.metro = metro;
    }

    public Double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }
}
