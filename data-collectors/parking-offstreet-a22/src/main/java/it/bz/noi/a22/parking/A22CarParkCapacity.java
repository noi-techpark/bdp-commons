// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

/*
 *  A22 Car Park Capacity
 *
 *  (C) 2021 NOI Techpark Südtirol / Alto Adige
 *
 *  changelog:
 *  2021-05-17  1.0 - chris@1006.org
 */

package it.bz.noi.a22.parking;

public class A22CarParkCapacity {

                                        // rif.: Softech - 2017-002 Autostrada del Brennero S.p.A.
                                        // Manutenzione ordinaria e straordinaria sistema di supervisione CAU - anno 2017 - N. doc: 2017-0046 Revisione: 9

    private Long id;                    // Identificativo univoco del parcheggio
    private Long stato;                 // Identificativo dello stato di occupazione del parcheggio Vedi tabella OCCUPAZIONE PARCHEGGIO 5.8.9
    private Long capienza;              // Numero massimo di parcheggi.
    private Long posti_liberi;          // Numero di posti disponibili. A null se questa informazione non è disponibile.

    @Override
    public String toString() {
        return "A22CarParkCapacity{" +
                "id=" + id +
                ", stato=" + stato +
                ", capienza=" + capienza +
                ", posti_liberi=" + posti_liberi +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStato() {
        return stato;
    }

    public void setStato(Long stato) {
        this.stato = stato;
    }

    public Long getCapienza() {
        return capienza;
    }

    public void setCapienza(Long capienza) {
        this.capienza = capienza;
    }

    public Long getPosti_liberi() {
        return posti_liberi;
    }

    public void setPosti_liberi(Long posti_liberi) {
        this.posti_liberi = posti_liberi;
    }
}
