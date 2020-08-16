package com.frank.filters.filter.helper;

import com.frank.filters.filter.advanced.AntiqueFilter;
import com.frank.filters.filter.advanced.BlackCatFilter;
import com.frank.filters.filter.advanced.CalmFilter;
import com.frank.filters.filter.advanced.CoolFilter;
import com.frank.filters.filter.advanced.CrayonFilter;
import com.frank.filters.filter.advanced.EmeraldFilter;
import com.frank.filters.filter.advanced.EvergreenFilter;
import com.frank.filters.filter.advanced.FairytaleFilter;
import com.frank.filters.filter.advanced.HealthyFilter;
import com.frank.filters.filter.advanced.LatteFilter;
import com.frank.filters.filter.advanced.NostalgiaFilter;
import com.frank.filters.filter.advanced.RomanceFilter;
import com.frank.filters.filter.advanced.SakuraFilter;
import com.frank.filters.filter.advanced.SketchFilter;
import com.frank.filters.filter.advanced.SkinWhitenFilter;
import com.frank.filters.filter.advanced.SunriseFilter;
import com.frank.filters.filter.advanced.SunsetFilter;
import com.frank.filters.filter.advanced.SweetsFilter;
import com.frank.filters.filter.advanced.TenderFilter;
import com.frank.filters.filter.advanced.WarmFilter;
import com.frank.filters.filter.advanced.WhiteCatFilter;
import com.frank.filters.filter.base.gpuimage.GPUImageFilter;

public class FilterFactory {

    private static FilterType filterType = FilterType.NONE;

    public static GPUImageFilter initFilters(FilterType type) {
        filterType = type;
        switch (type) {
            case WHITECAT:
                return new WhiteCatFilter();
            case BLACKCAT:
                return new BlackCatFilter();
            case SKINWHITEN:
                return new SkinWhitenFilter();
            case ROMANCE:
                return new RomanceFilter();
            case SAKURA:
                return new SakuraFilter();
            case ANTIQUE:
                return new AntiqueFilter();
            case CALM:
                return new CalmFilter();
            case EVERGREEN:
                return new EvergreenFilter();
            case HEALTHY:
                return new HealthyFilter();
            case COOL:
                return new CoolFilter();
            case EMERALD:
                return new EmeraldFilter();
            case LATTE:
                return new LatteFilter();
            case WARM:
                return new WarmFilter();
            case TENDER:
                return new TenderFilter();
            case SWEETS:
                return new SweetsFilter();
            case NOSTALGIA:
                return new NostalgiaFilter();
            case FAIRYTALE:
                return new FairytaleFilter();
            case SUNRISE:
                return new SunriseFilter();
            case SUNSET:
                return new SunsetFilter();
            case CRAYON:
                return new CrayonFilter();
            case SKETCH:
                return new SketchFilter();
            default:
                return null;
        }
    }

    public FilterType getCurrentFilterType() {
        return filterType;
    }
}
