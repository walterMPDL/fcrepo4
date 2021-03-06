/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.fcrepo.http.commons.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Range header parsing logic
 */
public class Range {

    private final long start;
    private final long end;
    private static Pattern rangePattern =
        Pattern.compile("^bytes\\s*=\\s*(\\d*)\\s*-\\s*(\\d*)");

    /**
     * Unbounded Range
     */
    public Range() {
        this(0, -1);
    }

    /**
     * Left-bounded range
     * @param start
     */
    public Range(final long start) {
        this(start, -1L);
    }

    /**
     * Left and right bounded range
     * @param start
     * @param end
     */
    public Range(final long start, final long end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Does this range actually impose limits
     * @return
     */
    public boolean hasRange() {
        return !(start == 0 && end == -1);
    }

    /**
     * Length contained in the range
     * @return
     */
    public long size() {
        if (end == -1) {
            return -1;
        } else {
            return end - start + 1;
        }
    }

    /**
     * Start of the range
     * @return
     */
    public long start() {
        return start;
    }

    /**
     * End of the range
     * @return
     */
    public long end() {
        return end;
    }

    /**
     * Convert an HTTP Range header to a Range object
     * @param source
     * @return
     */
    public static Range convert(final String source) {

        final Matcher matcher = rangePattern.matcher(source);

        if (!matcher.matches()) {
            return new Range();
        }

        matcher.matches();
        String from = matcher.group(1);
        String to = matcher.group(2);

        final long start;

        if (from.equals("")) {
            start = 0;
        } else {
            start = Long.parseLong(from);
        }

        final long end;
        if (to.equals("")) {
            end = -1;
        } else {
            end = Long.parseLong(to);
        }

        return new Range(start, end);
    }
}
