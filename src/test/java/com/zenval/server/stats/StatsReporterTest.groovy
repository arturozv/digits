package com.zenval.server.stats

import com.zenval.server.digit.DigitUniqueControl
import spock.lang.Specification

/**
 * Created by arturo on 20/05/17.
 */
class StatsReporterTest extends Specification {
    def StatsReporter statsreporter

    def setup() {}

    def "reportStats message generation"() {
        given:
        def uniqueDigitControl = Mock(DigitUniqueControl) {
            getProcessedSize() >> 10
            getDuplicatedSize() >> 1
        }
        statsreporter = new StatsReporter(uniqueDigitControl, 1);

        when: "first time"
            def result = statsreporter.reportStats()
        then:
            result == "Received 10 unique numbers, 1 duplicates. Unique total: 10"

        when: "next time"
            result = statsreporter.reportStats()
        then:
            result == "Received 0 unique numbers, 0 duplicates. Unique total: 10"
    }
}
