// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.augeg4.dto.toauge;

public class ProcessedResValToAuge {

    private final int id;

    private final double value;

    public ProcessedResValToAuge(int id, double value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ProcessedResValToAuge{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
