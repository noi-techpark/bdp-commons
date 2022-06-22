# e-mobility e-charging data quality checks

A simple python script to see if the data provided by the different e-charging stations data providers is saved correct in the Open Data Hub.

This is the first prototype and will fetch the data from the data provider and the corresponding data from the Open Data Hub amd do the checks.
In future with the new Architecture 2.0 of the Open Data Hub, the raw data of the data provider will be saved in the database and so the real data used to create the entries in the Open Data Hub can be used. So that data will be used and no second fetching from the data provider is needed.

It's also planned to adapt this quality checks for other data of the Open Data Hub.
