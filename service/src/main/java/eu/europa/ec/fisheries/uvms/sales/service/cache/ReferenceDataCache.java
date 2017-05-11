package eu.europa.ec.fisheries.uvms.sales.service.cache;

import eu.europa.ec.fisheries.schema.sales.SalesCategoryType;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.CountryCurrency;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.LocationCountry;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCode;
import eu.europa.ec.fisheries.uvms.sales.service.dto.cache.ReferenceCoordinates;

import javax.ejb.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ReferenceDataCache {
    private List<ReferenceCode> flagStates;
    private List<ReferenceCode> salesCategories;
    private List<ReferenceCode> salesLocations;
    private List<ReferenceCode> freshness;
    private List<ReferenceCode> presentations;
    private List<ReferenceCode> preservations;
    private List<ReferenceCode> distributionClasses;
    private List<ReferenceCode> usages;
    private List<ReferenceCode> currencies;
    private List<CountryCurrency> countryCurrency;
    private List<ReferenceCode> species;
    private List<ReferenceCode> weightMeans;
    private List<ReferenceCode> productUsages;
    private List<ReferenceCode> fishSizeCategory;
    private List<LocationCountry> locationCountries;
    private List<ReferenceCode> referenceOrganisation;
    private List<ReferenceCoordinates> referenceCoordinates;


    public ReferenceDataCache() {
        createLists();

        addCountryCodes();
        addSalesCategories();
        addSalesLocations();
        addSalesFreshness();
        addSalesPresentation();
        addSalesPreservation();
        addSalesDistributionClasses();
        addSalesUsages();
        addReferenceCurrencies();
        addCountryCurrencies();
        addReferenceSpecies();
        addWeightMeans();
        addProductUsages();
        addFishSizeCategories();
        addLocationCountries();
        addReferenceOrganisations();
        addReferenceCoordinates();
    }

    private void createLists() {
        flagStates = new ArrayList<>();
        salesCategories = new ArrayList<>();
        salesLocations = new ArrayList<>();
        freshness = new ArrayList<>();
        presentations = new ArrayList<>();
        preservations = new ArrayList<>();
        distributionClasses = new ArrayList<>();
        usages = new ArrayList<>();
        currencies = new ArrayList<>();
        countryCurrency = new ArrayList<>();
        species = new ArrayList<>();
        weightMeans = new ArrayList<>();
        productUsages = new ArrayList<>();
        fishSizeCategory = new ArrayList<>();
        locationCountries = new ArrayList<>();
        referenceOrganisation = new ArrayList<>();
        referenceCoordinates = new ArrayList<>();
    }

    private void addReferenceCoordinates() {
        referenceCoordinates.add(new ReferenceCoordinates("BEOST",51.21667, 2.91666666666667));
        referenceCoordinates.add(new ReferenceCoordinates("BEZEE",51.3189468, 3.206850700000018));
        referenceCoordinates.add(new ReferenceCoordinates("GBLON",51.5, 0.00));
        referenceCoordinates.add(new ReferenceCoordinates("BENIE",51.133333, 2.75));
    }

    private void addReferenceOrganisations() {
        referenceOrganisation.add(new ReferenceCode("XEU", "European Commission"));
        referenceOrganisation.add(new ReferenceCode("XFA", "CFCA"));
        referenceOrganisation.add(new ReferenceCode("XNW", "NAFO"));
        referenceOrganisation.add(new ReferenceCode("XNE", "NEAFC"));
        referenceOrganisation.add(new ReferenceCode("XIC", "ICCAT"));
        referenceOrganisation.add(new ReferenceCode("XCA", "CCAMLR"));
    }

    private void addLocationCountries() {
        locationCountries.add(new LocationCountry("BEOST", "BEL"));
        locationCountries.add(new LocationCountry("BEBRU", "BEL"));
        locationCountries.add(new LocationCountry("NLAMS", "NDL"));
    }


    private void addFishSizeCategories() {
        fishSizeCategory.add(new ReferenceCode("1", "According to AnnexII of Council R."));
        fishSizeCategory.add(new ReferenceCode("2", "According to AnnexII of Council R."));
        fishSizeCategory.add(new ReferenceCode("3", "According to AnnexII of Council R."));
        fishSizeCategory.add(new ReferenceCode("4", "According to AnnexII of Council R."));
        fishSizeCategory.add(new ReferenceCode("N/A", "Species not included in the common marketing standards"));
    }

    private void addProductUsages() {
        productUsages.add(new ReferenceCode("HCN", "Commercial direct human consumption"));
        productUsages.add(new ReferenceCode("HCN-INDIRECT", "Commercial indirect human consumption"));
        productUsages.add(new ReferenceCode("IND", "Industrial"));
        productUsages.add(new ReferenceCode("BAI", "Live bait"));
        productUsages.add(new ReferenceCode("ANF", "Animal Feed"));
        productUsages.add(new ReferenceCode("WST", "Waste"));
        productUsages.add(new ReferenceCode("WDR", "Withdrawn"));
        productUsages.add(new ReferenceCode("COV", "Carry Over"));
        productUsages.add(new ReferenceCode("UNK", "Unknown"));
    }

    private void addWeightMeans() {
        weightMeans.add(new ReferenceCode("ONBOARD", "Onboard weighing system"));
        weightMeans.add(new ReferenceCode("WEIGHED", "Weighed"));
        weightMeans.add(new ReferenceCode("ESTIMATED", "Estimated live weight"));
        weightMeans.add(new ReferenceCode("COUNTED", "Counted number of units"));
        weightMeans.add(new ReferenceCode("SAMPLING", "Estimated live weight based on sampling"));
        weightMeans.add(new ReferenceCode("STEREOSCOPIC", "Weight estimations based on stereoscopic devices"));
    }

    private void addReferenceSpecies() {
        species.add(new ReferenceCode("SAL", "Salmo salar"));
        species.add(new ReferenceCode("COD", "Gadus Morhua"));
        species.add(new ReferenceCode("SQS", "Martialia hyadesi"));
        species.add(new ReferenceCode("LEM", "Microstomus kitt"));
        species.add(new ReferenceCode("TUR", "Psetta maxima"));
        species.add(new ReferenceCode("SOL", "Solea solea"));
        species.add(new ReferenceCode("AAA", "Acipenser naccarii"));
    }

    private void addCountryCurrencies() {
        countryCurrency.add(new CountryCurrency("BEL", "EUR"));
        countryCurrency.add(new CountryCurrency("NLD", "EUR"));
        countryCurrency.add(new CountryCurrency("FIN", "EUR"));
        countryCurrency.add(new CountryCurrency("DEU", "EUR"));
        countryCurrency.add(new CountryCurrency("FRA", "EUR"));
        countryCurrency.add(new CountryCurrency("GRC", "EUR"));
        countryCurrency.add(new CountryCurrency("IRL", "EUR"));
        countryCurrency.add(new CountryCurrency("ESP", "EUR"));
    }

    private void addReferenceCurrencies() {
        currencies.add(new ReferenceCode("EUR", "€"));
        currencies.add(new ReferenceCode("PND", "£"));
        currencies.add(new ReferenceCode("USD", "$"));
    }

    private void addSalesUsages() {
        usages.add(new ReferenceCode("HCN", "Human consumption"));
        usages.add(new ReferenceCode("WST", "Waste"));
    }

    private void addSalesDistributionClasses() {
        distributionClasses.add(new ReferenceCode("LSC", "Legal size catch"));
        distributionClasses.add(new ReferenceCode("BMS", "Below minimum size"));
    }

    private void addSalesPreservation() {
        preservations.add(new ReferenceCode("FRE", "Fresh"));
        preservations.add(new ReferenceCode("ALI", "Alive"));
    }

    private void addSalesPresentation() {
        presentations.add(new ReferenceCode("GUT", "Gutted"));
        presentations.add(new ReferenceCode("WHL", "Whole"));
    }

    private void addSalesFreshness() {
        freshness.add(new ReferenceCode("E", "Extra"));
        freshness.add(new ReferenceCode("V", "Live"));
    }

    private void addSalesLocations() {
        salesLocations.add(new ReferenceCode("BEOST", "BEOST - Ostend"));
        salesLocations.add(new ReferenceCode("BEZEE", "BEZEE - Zeebrugge"));
        salesLocations.add(new ReferenceCode("GBLON", "GBLON - London"));
        salesLocations.add(new ReferenceCode("DEMEE", "DEMEE - Emden"));
        salesLocations.add(new ReferenceCode("NLBRS", "NLBRS - Breskens"));
    }

    private void addSalesCategories() {
        salesCategories.add(new ReferenceCode(SalesCategoryType.FIRST_SALE.toString(), "First Sale"));
        salesCategories.add(new ReferenceCode(SalesCategoryType.NEGOTIATED_SALE.toString(), "Negotiated Sale"));
        salesCategories.add(new ReferenceCode(SalesCategoryType.VARIOUS_SUPPLY.toString(), "Various Supply"));
    }

    private void addCountryCodes() {
        flagStates.add(new ReferenceCode("BEL", "Belgium"));
        flagStates.add(new ReferenceCode("FIN", "Finland"));
        flagStates.add(new ReferenceCode("DEU", "Germany"));
        flagStates.add(new ReferenceCode("FRA", "France"));
        flagStates.add(new ReferenceCode("GRC", "Greece"));
        flagStates.add(new ReferenceCode("IRL", "Ireland"));
        flagStates.add(new ReferenceCode("NLD", "Netherlands"));
        flagStates.add(new ReferenceCode("ESP", "Spain"));
    }

    public List<ReferenceCode> getFlagStates() {
        return flagStates;
    }

    public List<ReferenceCode> getSalesCategories() {
        return salesCategories;
    }

    public List<ReferenceCode> getSalesLocations() {
        return salesLocations;
    }

    public List<ReferenceCode> getFreshness() {
        return freshness;
    }

    public List<ReferenceCode> getPresentations() {
        return presentations;
    }

    public List<ReferenceCode> getPreservations() {
        return preservations;
    }

    public List<ReferenceCode> getDistributionClasses() {
        return distributionClasses;
    }

    public List<ReferenceCode> getUsages() {
        return usages;
    }

    public List<ReferenceCode> getCurrencies() {
        return currencies;
    }

    public List<CountryCurrency> getCountryCurrency() {
        return countryCurrency;
    }

    public List<ReferenceCode> getSpecies() {
        return species;
    }

    public List<ReferenceCode> getWeightMeans() {
        return weightMeans;
    }

    public List<ReferenceCode> getProductUsages() {
        return productUsages;
    }

    public List<ReferenceCode> getFishSizeCategory() {
        return fishSizeCategory;
    }

    public List<LocationCountry> getLocationCountries() {
        return locationCountries;
    }

    public List<ReferenceCode> getReferenceOrganisation() {
        return referenceOrganisation;
    }

    public List<ReferenceCoordinates> getReferenceCoordinates() {
        return referenceCoordinates;
    }
}
