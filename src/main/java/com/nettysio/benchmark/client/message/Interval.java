package com.nettysio.benchmark.client.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author Yulin Chen
 * @since 11/28/14
 */
@Data
@AllArgsConstructor
public class Interval {
    int number;
    TimeUnit timeunit;

    public static Interval defaultInterval() {
        return new Interval(1000, TimeUnit.MILLISECONDS);
    }
}
