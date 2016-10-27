/*
 * Copyright 2016 Imply Data, Inc.
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

package io.imply.druid.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.metamx.common.guava.Sequence;
import com.metamx.common.guava.Sequences;
import io.druid.query.Druids;
import io.druid.query.Result;
import io.druid.query.filter.AndDimFilter;
import io.druid.query.filter.DimFilter;
import io.druid.query.filter.SelectorDimFilter;
import io.druid.query.select.EventHolder;
import io.druid.query.select.PagingSpec;
import io.druid.query.select.SelectQuery;
import io.druid.query.select.SelectResultValue;
import org.joda.time.Interval;

import java.util.List;

public class ExampleMain
{
  public static void main(String[] args) throws Exception
  {
    final String host = args.length == 0 ? "localhost:8082" : args[0];
    try (final DruidClient druidClient = DruidClient.create(host)) {
      // Create a simple select query using the Druids query builder.
      final int threshold = 50;
      final SelectQuery selectQuery = Druids
          .newSelectQueryBuilder()
          .dataSource("wikiticker")
          .intervals(ImmutableList.of(new Interval("1000/3000")))
          .filters(
              new AndDimFilter(
                  ImmutableList.<DimFilter>of(
                      new SelectorDimFilter("countryName", "United States", null),
                      new SelectorDimFilter("cityName", "San Francisco", null)
                  )
              )
          )
          .dimensions(ImmutableList.of("page", "user"))
          .pagingSpec(new PagingSpec(null, threshold))
          .build();

      // Fetch the results.
      final long startTime = System.currentTimeMillis();
      final Sequence<Result<SelectResultValue>> resultSequence = druidClient.execute(selectQuery);
      final List<Result<SelectResultValue>> resultList = Sequences.toList(
          resultSequence,
          Lists.<Result<SelectResultValue>>newArrayList()
      );
      final long fetchTime = System.currentTimeMillis() - startTime;

      // Print the results.
      int resultCount = 0;
      for (final Result<SelectResultValue> result : resultList) {
        for (EventHolder eventHolder : result.getValue().getEvents()) {
          System.out.println(eventHolder.getEvent());
          resultCount++;
        }
      }

      // Print statistics.
      System.out.println(
          String.format(
              "Fetched %,d rows in %,dms.",
              resultCount,
              fetchTime
          )
      );
    }
  }
}
