package id.ac.uns.posbindu.form

import id.ac.uns.posbindu.R
import id.ac.uns.posbindu.form.Question.MultipleChoiceQuestion
import id.ac.uns.posbindu.form.Question.ShortAnswerQuestion
import java.util.*

private val ID_QUEUE: Queue<String> = LinkedList(
    listOf(
        "aByPXQCspdbBPZnUzicG",
        "XFzYpVWXVRBZzONqleiX",
        "XxQJgkVQJzmeArEeuupr",
        "NphVqerqGcKXexIWYfZX",
        "zWtAetrVvZSnOkSbaFGF",
        "zfcfRhZbtLmuJdxsCCsO",
        "IWddTWXafKKxvpswKcJj",
        "lgoHoNaAzPHpaiEpTNQV",
        "wzZOxwApinOkCUUQjtmm",
        "NrsrFtLHUSJAPjUAXPsh",
        "enMQSaEboJwHidyJLkHd",
        "rCfKMaAoZCKpfyRldPRI",
        "EJaUUbgZsuZTKvUESgWm",
        "BIScKidpZUEKEBgkdHEi",
        "ymcoQZEwXgKpOXPqpaCd",
        "YAkGUuLjEpOLYpqMJIKE",
        "yIkxpgzjnPpjwHlfLzhG",
        "zZBklmhPzDDBGXvtVxyZ",
        "KVhXdvYGdKjOTOLtenPl",
        "ZnFZMvsmhFRpvUtftech",
        "WfehvxIAGsdDvbVvfeHu",
        "rFdiTHbZWHCPCIsIDbQh",
        "qxNMeMacDIRTKrcwQyNn",
        "FUXlLMRTnzKyLoULOicD",
        "OACwnKCcAVfsZOykKtmP",
        "xvqEayAFhvizuEEVQeKS",
        "svjDmyYhFqmQMOMnbtPY",
        "YjlXQehYAkBbnPgVezGa",
        "jVGflimnUNHPvnbEXnsN",
        "rMmENvJjaLVcCDIEnIIn",
        "hyzWxawafkJjzThAmzEJ",
        "CioEKJlHFNLMUEMcKCYt",
        "UnWdGBkZeSXDCsXuaoIV",
        "NGbjSwPszmpjsDaUTaHA",
        "evIExbiSQgxmuONsqoKK",
        "rDcmuBTNmYJPbGsAFXBe",
        "qQZZeaXkPoACVVJRuvgX",
        "YPPcNoHmSSQeBWBEIzBD",
        "ZvLSuTcOgXchGMeQwsVn",
        "QlOirpHycGoTrlBEZEbE",
        "KqNlPrENADCktzhlJEKu",
        "QONLlesFrazpEysHduvv",
        "bCwpKfgDKbRKdYUncGje",
        "cCcPRzKWhUccPXrXbOfp",
        "atLXgWAtUNDTuNihyojm",
        "SyKSqekryebtiosrAhjm",
        "ijTlCOEeCjNGoLkOcpoQ",
        "cehCiGccawrGEwKGjAKB",
        "RDumYcizEOTFKMinNCbZ",
        "oZAkIxjSiBwWZTTWfznY",
        "WXojNXwJTqBMQbSOcCgU",
        "xaGnSQUUqlSixQqQFGEH",
        "iVBXKYCvPdIUomPPkhzr",
        "xWjgVFyGdvuTzTTgYIBC",
        "RpzOwSozSvGpwkIiSEaQ",
        "HWIBGQdTZCPEqNUDKHkS",
        "tUQPGXdKOyevRFCfUoEJ",
        "llBkXUTcVMatqYIwEjlg",
        "FiozzdvfuAkJqIQsHiME",
        "otSkcpEDRiYhNXpvHhwH",
        "pgUuiMAjPuhUTqkxbVFt",
        "rBsVHPccixzfONHMTZqk",
        "DVcDfyinkQNgxEswTYNi",
        "vVsdBcwEfOjOSdIZmTaI",
        "kDqBjESYcgCEULaWiNbJ",
        "QGgJwpceZZuvfZlhDEdj",
        "UMmWVOyfSBylhEcTQsbO",
        "OqOTIoJMezKYZHCkEjmR",
        "aVUCwFfQlElDNwsrxlCx",
        "lmKeqVolSLQUTKpJJyxB",
        "hDnTaAclPJXPeWzeahtu",
        "JcntZSrPvmVCaFMDdlLO",
        "jYMTsmnqQhnEqzHQMvJr",
        "mdfQdcTRJXPWprCizxbZ",
        "UdcIOYKpovIZXDrettjf",
        "aNIVuFArcZmGUrxnDloD",
        "TxENUrmPubxtMttepcKG",
        "sCimboyBvQewAwlHNQIV",
        "LhYAkVkHOISenKHXyams",
        "qvtGMpkJORajdjNtfLNh",
        "czwgYBheNtMMYQpSmvRJ",
        "dQpGIwqkqxMHGAxHWslW",
        "kwlOUzpolvqKDTXlFjYl",
        "DVWPdtwUrBnqMnHWGTXf",
        "CgZBvAcaUIuMEfvAJcho",
        "CjwBBkiimRpOqWIfqCcu",
        "wtLnZQZUQouHjFVeZiXq",
        "HqRYiXHSxmqnpIwmodCS",
        "AWtceyiJBtrhbzWaZhuf",
        "TggNGyjjFoWJouCNtkog",
        "IHCnFpRcpfaTjtxmtbsR",
        "ZkzWlgmxOosOBALUWKNT",
        "RgCwrnbkvxdlJHfujBEg",
        "IvnwpmZkRKNFZSAlPbGm",
        "VrymXtRNvanbNTxYwJik",
        "eesQeOpTDfjKTnwnUJhe"
    )
)

val STUB_FORM_LIST = listOf(
    Form(
        "kZCJjJWyUEbeRlBajfaA",
        R.drawable.illustration_doctors,
        "Riwayat medis",
        "Pilih penyakit yang anda pernah didiagnosa",
        listOf(
            Section(
                ID_QUEUE.remove(),
                "Diabetes mellitus",
                "CAT_A group of diseases that result in too much sugar in the blood (high blood glucose).",
                false,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Sudah berapa lama anda didiagnosa diabetes mellitus/kencing manis?",
                        true,
                        listOf("< 1 tahun", "3 – 5 tahun", "1 – 3 tahun", "> 5 tahun")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Selama ini anda memeriksakan sakit ke mana?",
                        true,
                        listOf("Dokter", "Bidan", "Mantri")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Kapan anda memeriksakan diri terkait penyakit anda ini?",
                        true,
                        listOf(
                            "Tidak pernah",
                            "Saat ada keluhan",
                            "Setiap bulan",
                            "Setiap 3 bulan",
                            "Setiap 6 bulan"
                        )
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Obat apa yang anda dapatkan?",
                        true,
                        listOf(
                            "Metformin",
                            "Glibenclamide",
                            "Insulin",
                            "Dua macam obat",
                            "Tidak tahu"
                        )
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Hipertensi",
                "CAT_A condition in which the force of the blood against the artery walls is too high.",
                false,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Sudah berapa lama anda didiagnosa hipertensi/darah tinggi?",
                        true,
                        listOf("< 1 tahun", "3 – 5 tahun", "1 – 3 tahun", "> 5 tahun")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Selama ini anda memeriksakan sakit ke mana?",
                        true,
                        listOf("Dokter", "Bidan", "Mantri")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Kapan anda memeriksakan diri terkait penyakit anda ini?",
                        true,
                        listOf(
                            "Tidak pernah",
                            "Saat ada keluhan",
                            "Setiap bulan",
                            "Setiap 3 bulan",
                            "Setiap 6 bulan"
                        )
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Obat apa yang anda dapatkan?",
                        true,
                        listOf(
                            "Amlodipine",
                            "Captopril",
                            "Valsartan",
                            "Dua macam obat",
                            "Tidak tahu"
                        )
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Penyakit jantung",
                "Heart conditions that include diseased vessels, structural problems and blood clots.",
                false,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Sudah berapa lama anda didiagnosa penyakit jantung?",
                        true,
                        listOf("< 1 tahun", "3 – 5 tahun", "1 – 3 tahun", "> 5 tahun")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Selama ini anda memeriksakan sakit ke mana?",
                        true,
                        listOf("Dokter", "Bidan", "Mantri")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Kapan anda memeriksakan diri terkait penyakit anda ini?",
                        true,
                        listOf(
                            "Tidak pernah",
                            "Saat ada keluhan",
                            "Setiap bulan",
                            "Setiap 3 bulan",
                            "Setiap 6 bulan"
                        )
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Obat apa yang anda dapatkan?",
                        true,
                        listOf(
                            "Amlodipine",
                            "Captopril",
                            "Valsartan",
                            "Dua macam obat",
                            "Tidak tahu"
                        )
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Stroke",
                "Damage to the brain from interruption of its blood supply.",
                false,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Sudah berapa lama anda didiagnosa stroke?",
                        true,
                        listOf("< 1 tahun", "3 – 5 tahun", "1 – 3 tahun", "> 5 tahun")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Selama ini anda memeriksakan sakit ke mana?",
                        true,
                        listOf("Dokter", "Bidan", "Mantri")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Kapan anda memeriksakan diri terkait penyakit anda ini?",
                        true,
                        listOf(
                            "Tidak pernah",
                            "Saat ada keluhan",
                            "Setiap bulan",
                            "Setiap 3 bulan",
                            "Setiap 6 bulan"
                        )
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Apakah anda sudah dapat melakukan aktivitas kembali?",
                        true,
                        listOf("Belum", "Sudah membaik")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Apakah anda pernah mengalami serangan stroke kembali?",
                        true,
                        listOf("Ya", "Tidak")
                    )
                )
            ),
        )
    ),
    Form(
        "mfkuiuCVnoIfdGqFEsVs",
        R.drawable.illustration_diet,
        "Faktor risiko",
        "",
        listOf(
            Section(
                ID_QUEUE.remove(),
                "Diet",
                "",
                true,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Dalam sehari, berapa banyak anda mengonsumsi buah dan sayur?",
                        true,
                        listOf("< 1 porsi", "1 – 3 porsi", "3 – 5 porsi", "> 5 porsi")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Dalam sehari, berapa banyak anda mengonsumsi garam?",
                        true,
                        listOf(
                            "< 1 sendok makan",
                            "1 – 3 sendok makan",
                            "3 – 5 sendok makan",
                            "> 5 sendok makan"
                        )
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Dalam sehari, berapa banyak anda mengonsumsi gula?",
                        true,
                        listOf(
                            "< 1 sendok makan",
                            "1 – 3 sendok makan",
                            "3 – 5 sendok makan",
                            "> 5 sendok makan"
                        )
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Dalam sehari, berapa banyak anda mengonsumsi minyak?",
                        true,
                        listOf(
                            "< 1 sendok makan",
                            "1 – 3 sendok makan",
                            "3 – 5 sendok makan",
                            "> 5 sendok makan"
                        )
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Aktivitas Fisik",
                "",
                true,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Apakah anda rutin berolahraga?",
                        true,
                        listOf("Ya", "Tidak")
                    ),
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Berapa menit anda berolahraga dalam satu minggu?",
                        true,
                        listOf(
                            "< 50 menit",
                            "50 – 100 menit",
                            "100 – 150 menit",
                            "> 150 menit"
                        )
                    ),
                    ShortAnswerQuestion(
                        ID_QUEUE.remove(),
                        "Olahraga apa yang anda lakukan?",
                        true,
                        ShortAnswerQuestion.Type.STRING,
                        ""
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Rokok",
                "",
                true,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Apakah anda merokok?",
                        true,
                        listOf("Ya", "Tidak")
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Alkohol",
                "",
                true,
                listOf(
                    MultipleChoiceQuestion(
                        ID_QUEUE.remove(),
                        "Apakah anda minum alkohol?",
                        true,
                        listOf("Ya", "Tidak")
                    )
                )
            )
        )
    ),
    Form(
        "fdRStsHRvClNJTDIinwL",
        R.drawable.illustration_fitness_stats,
        "Pemeriksaan fisik",
        "",
        listOf(
            Section(
                ID_QUEUE.remove(),
                "Indeks massa tubuh",
                "",
                true,
                listOf(
                    ShortAnswerQuestion(
                        ID_QUEUE.remove(),
                        "Berat badan",
                        true,
                        ShortAnswerQuestion.Type.INTEGER,
                        "dalam kilogram (kg)"
                    ),
                    ShortAnswerQuestion(
                        ID_QUEUE.remove(),
                        "Tinggi badan",
                        true,
                        ShortAnswerQuestion.Type.INTEGER,
                        "dalam centimeter (cm)"
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Lingkar perut",
                "",
                true,
                listOf(
                    ShortAnswerQuestion(
                        ID_QUEUE.remove(),
                        "Lingkar perut",
                        true,
                        ShortAnswerQuestion.Type.INTEGER,
                        "dalam centimeter (cm)"
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Tekanan darah",
                "",
                true,
                listOf(
                    ShortAnswerQuestion(
                        ID_QUEUE.remove(),
                        "Tekanan darah",
                        true,
                        ShortAnswerQuestion.Type.INTEGER,
                        "dalam mmHg"
                    )
                )
            ),
            Section(
                ID_QUEUE.remove(),
                "Gula darah sewaktu",
                "",
                true,
                listOf(
                    ShortAnswerQuestion(
                        ID_QUEUE.remove(),
                        "Gula darah sewaktu",
                        true,
                        ShortAnswerQuestion.Type.INTEGER,
                        ""
                    )
                )
            )
        )
    )
)