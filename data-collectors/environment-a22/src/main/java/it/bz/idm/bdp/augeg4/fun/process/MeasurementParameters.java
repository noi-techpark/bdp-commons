// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.fun.process;

import java.math.BigDecimal;

/**
 * Parameters used by the MeasurementProcessor for a specific Measurement
 */
public class MeasurementParameters {

    private MeasurementParametersId id;

    private BigDecimal a;
    private BigDecimal b;
    private BigDecimal c;
    private BigDecimal d;
    private BigDecimal e;
    private BigDecimal f;

    public MeasurementParameters(MeasurementParametersId id, BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d, BigDecimal e, BigDecimal f) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public BigDecimal getA() {
        return a;
    }

    public BigDecimal getB() {
        return b;
    }

    public BigDecimal getC() {
        return c;
    }

    public BigDecimal getD() {
        return d;
    }

    public BigDecimal getE() {
        return e;
    }

    public BigDecimal getF() {
        return f;
    }
}
