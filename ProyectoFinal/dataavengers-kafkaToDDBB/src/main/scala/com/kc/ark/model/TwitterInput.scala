package com.kc.ark.model


case class TwitterInput(dateFormatted: String,
                        sourceQuery: String,
                        hashtags: List[String],
                        id: Long,
                        id_str: String,
                        retweet_count: Long,
                        favorite_count: Long,
                        favorited: Boolean,
                        retweeted: Boolean,
                        text: String,
                        created_at: String,
                        lang: String)




