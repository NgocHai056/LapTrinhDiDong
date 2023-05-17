    package com.example.shopee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.shopee.R;
import com.example.shopee.adapter.CategoriesAdapter;
import com.example.shopee.adapter.ProductsAdapter;
import com.example.shopee.model.Categories;
import com.example.shopee.model.MessageModel;
import com.example.shopee.model.Products;
import com.example.shopee.model.ProductsModel;
import com.example.shopee.retrofit.ApiShopee;
import com.example.shopee.retrofit.RetrofitClient;
import com.example.shopee.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

    public class MainActivity extends AppCompatActivity {

        ViewFlipper viewFlipper;
        GridView gv_categories;
        RecyclerView rcv_pd;
        FrameLayout btn_cart;
        ImageView btn_user;
        TextView searchView;

        CategoriesAdapter categoriesAdapter;
        ProductsAdapter productsAdapter;
        List<Categories> categories;
        List<Products> products;
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        ApiShopee apiShopee;
        NotificationBadge badge;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            apiShopee = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiShopee.class);

            getToken();
            Mapping();
            if (isConnected(this)) {
                ActionViewFlipper();
                getCategories();
                getProcducts();
                getClickEventCategory();
            } else {
                Toast.makeText(getApplicationContext(), "Không có Internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
            }
        }

        private void SetBadge() {
            int totalItem = 0;
            for (int i=0; i<Utils.ListCart.size(); i++){
                totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
            }
            if (totalItem != 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(totalItem));
            }
            else {
                badge.setVisibility(View.GONE);
            }
        }

        private void getToken() {
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if(!TextUtils.isEmpty(s)) {
                                compositeDisposable.add(apiShopee.updateToken(Utils.UserCurent.getId(), s)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                messageModel -> {
                                                },
                                                throwable -> {
                                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                        ));
                            }
                        }
                    });
        }

        private void getClickEventCategory() {
            gv_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent ctgActivity = new Intent(getApplicationContext(), CategoriesActivity.class);
                    ctgActivity.putExtra("type", (i+1));

                    ctgActivity.putExtra("title", ((TextView)view.findViewById(R.id.item_name)).getText());
                    startActivity(ctgActivity);
                }
            });
        }

        private void getProcducts() {
            compositeDisposable.add(apiShopee.getProcducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            productsModel -> {
                                if (productsModel.isSuccess()) {
                                    products = productsModel.getResult();
                                    productsAdapter = new ProductsAdapter(getApplicationContext(), products);
                                    rcv_pd.setAdapter(productsAdapter);
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), "Không kết nối được với sever" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        }

        

        private void ActionViewFlipper() {

            /*List<String> listBanner = new ArrayList<>();
            listBanner.add("https://lh3.googleusercontent.com/eRxFRASvSigxxhj3NRxnuhqzlmSvpQInVclmoTLhsWusLvRglpsoCZSad__z94_p4ldjD0FNV4uUAEsU3Y2f-LI0cLhUnHIsh4g5y_aoN7tNjTbq7f7uW1CcQqk_GfrimJsEPjGuDYr_qaEIdILYxqdTmMk65W7hKeBSsz93pkBvzyU0va4MNxW3mUpkCkE7hhLv7U4BvsDiYKyDJYpGixlCKkmH4iF8cpRd9tZhSSo-NSvWj43XtuJBHv1ARPHscy5kJAw0V1SaytRmUk0os6HzGmxrkxcMgE0RhcCU1HgvQXw3QAPCR_aWt1AfNMk9La361-f4TcvM-0F2qWHgQ5OG07Mg8zuYOCzQ1ouRvBAQY_pp-THszWzMHRScmnu_uZA_nTb5aryJDFEG3pSMgzloU35BOQ_p6GcOPB5X9_ALhK5RzbHDDOtU8lzVYsaZxqYoEMnY4Sk5QjSilIAPqHmm30-TimGZKuv7LESUE1t2aoolvb7c4Cd3SajOyiKM9Q2qn_rznknXSf8Rl-qCbeNMJ8lXoCBKiWzkrcrxxPfaSsntsnwO9X5b1fhLSgC-V4j5hD5VNHLpiTiXh8enCBTRiX9c3UyoMA39Ptzh0OWfK8gsSWK5ZTjDDseoBywM2GkRnyxqP3TpxzFlCkYvI8N5bYD1EjDmATyqaKB5bXmeLL0NaILxqErFs-PU8Zps4efZsRw6qMZCp2Sp7oP08arOwGul7yc3OQAzzYTpwPxxDOHErZ5MLA_EK1YSvEFjblt_M30tIK45H0s58t75GfxaFPFAyoYKCCaGaLtMW_1VlraKyJTbcnqUwzWD_82cX9ZyAbuJpsT-uQY_cnZGJbIPXZpjcRucoT-UY_ppaHDwqTwYwUlpoxEFivsvgUTFY63afIbShszWOMCGev7yKqQg7qXZR23f390mS-XqEyg=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/vGG2WpzEqqKgqeOE8pYCiG-eo36KuZHeFU3-XK2VVIahMwdmtT-WM_EuGdSdciMm26P6zgzhQTGUSWXfUf6w5R9YsCO3LXZOZ_Qq_qbD5-2vLEIRWO2sq_IThgGWXJfCZff22Nwq7JmeTd36iqIzCesLM-N4QEABxxcbrVN5KuZMTlca8LD-c6M50rT-NVdg-8RdV8Y0_8tyerVviV6ggdfQXfM0yiWKVq0AsOGJTwLLjyC-eDsslqq8Pvz1gKtpma5oxK1tTGDHm4ZlE3rtTVaWKuDrGJk5Hn7uN58CR9YB1FkDRKtqX8aoP47Cr0-6X39kWFFxS5rzxaRuvaYZRgQk4Da1Gwkgm0eKqJCSzTzgMfjUv9ioWCTJajzPvnyI1DrhdFU1UKAwhQEEHcb2dYW-GmZLGRsa0lyDGrc_eEDa7ExgubtG-OrAwocAmWAadVYBielCcCtiQOfm04ljLMO9PlKxD9IKbCzYqM7w5UlXdgqYmIs-ytVMw8acfjVOLf7FE67pQBuuYJBf5dvAPp6ah0WAApwEEr9czWwOBHnMlUVuoHEhbAzUkJIXPb9CFOxCLbJFFgbnpCKrFJlt1CPLHDHNIH8AgStaKsTMOF8nDmKjw8PAq7CH7YUzzPodIC0swa8I7hbLDiOMltFGYW-o9MxmhtlthrdHgLqy_f6o5CjRAWd_a9j6_lu4q8_8EW7oGKoxMzTyFtxCk69sfa0UdshEJE4OsSVOh6dxS4JkBHq8dzMy0bfzEOskGrE4TQwuXnXTvqrIrwbAN8Tt_uWPuSj9qhAJ9KtpscLT9voaPwc-SjE3JLxecOpfP5gTlMpPckpqy6G8IY8PRibgDALbVbFeI89xUQcVnRifA-fQyY-YEIpa9sGKA-ZpVCU15-MGCHARvUj31Vy92po1_sQZWjEHlL_l4Kn501vfAc4=w405-h216-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/dg838fHwhv3pJ4zpsxsVZa0ErzzyBt7HHO0uTE7VyPNbt0Z6c8l2hNuq0trVT2CI6D02eHHTYnU5FIuCY7xF36yyy3g22TF0WrH3kjmG1h9YIGmFSn3T-x8u05LrYbhpMvCtr0f_jb5yEC4zL1m6Itd81yx2F8dxWelyRMTXVfP-ifTrQwZAstBgbAgcpUuO5G3dVMfTDOJZV6x5qCui6koss0QZY46AN8TIsfexUHCH94KpzVhOgi_5wPTmITw6CAtumfAlzyJSsFlz-318ZQkuwXuBhnGp4BuDx4mZqx-EyhJqZUQh4zzxePLZO39ttv6Hnmoq9YF-DjYguAC6-xkOQfdbKE1BLu8i7MIiInEiWxP95a1Jo3dkzHKpn3UFZZPSuZG4onsGie-rm_A4nautT_rO0w2a2ReHE5edIsxTWQi_ZT8XsW1bBEk7SzYAoPnTUs4DVvOtD6R5aDExF9Qcvm5dQUlCvPEGruPKhZmutbOqnhzlWnOhrxKKnrfKE-N0AczH8nmKBPWSgQ7k-9_DGYiUn_wRwTW9hSPnDOjk7MmAM_Mw0npCWqCEDhxDYq50HZOYmr5V5YDTi3rf2X-C54IsPNjpl8uhLzIVwdPXw7bdGN9P6dmkDs_xo6iNluGNu7QkD3oifjMO5zNaoiSJ22hHbhbUGXLVlwkGqto1X7a_3xBehiYge2BTvOK7mB9hxameGuLHZy_6ksu4_bHi9-7jTSjtPfPhmpeEJH63hZ-bMYDbx0XNe8pqZpcQejhdX4BdiUUkoifxpg-2u1qcsXLtMKzO9YxU7VBb8qrG3eF32hsUZnrY0YBEA5FHIGidPyBWfyVOYwfqQvrMk0j02SYgkTlh3XNFHghSk-U-RzDuIdwUbbGYH_geAsdjskV4nRNpqy-ud_paQN2-drAM9L23YaR9hun7sdzNzuA=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/9RF-MlpgyrBxYLRgeF47xhN2T9p5leORBl1KFLTJCihpiJltQckFAJEjOwvHSdozgIuqxXn2f4XiiOCY-KR7cAU86eZk9vjipsELl-ESqyU31aeJKWbbkzMLt2qgHpN1HVoIbxemmGwk1segGkAfLqhbkQYguZGHVyQUfT_cHZHmeWNf0zv6sLZ2QLVEmZpSGWGodtS7ijfn7bLkHxtV13Cfm06q3_aQqeEYGRjAbOV2XQsOfwcVyZKIcedWKRlKUBUBRuYOejR0emosykr7pihA-ygBDYtFw0navoQpynpRXs7uWoZraVYnROF9ygVWB7qRFw9WmTqDbQcsmiEUQHan-Mgqv5D-a4uBBn5afXKnJFsHGqMjfX-ZNgT1IRfLA5jm4aqvanJzAmtaFFPNgvqOoVFeJIXKKPn9z0KatYvC8bwfDKTBiLA3uElXzx0Y3RARqunjlmarF7kJ9GHpCYDS0T37fgyvG__7HX8wqD5TvKNABy2EJmGHeUHYx_aXyZI4IkqpwVlZQaJaBro2ve-Bl0fJVelzUIaYYY966JjXKH1yP7U6rQvMe1DBSF5M59HAKZQcOnx4ZMGcvo7lmGyg5u9Jp4RKqhvxL4NnYYxgNsa0MzH9uZdCOdC8C2TUUbstEPvzxHw8UQp4c4NCOwuJqYulgFvgXgCuziIPttLXYGCnUKQKdWhl_QPt2eUgk_5g5_S_eQZceNc9yioQayj-t4vTHgRM5DQpmnVuU_oPVk96TCzjBuLAY_AqlXssY_h6jAiZy0Wv8YubIZm35aiVvlMIMSeWpGb5u7Elagaj8cId_ucESQZ-BBFsilE-xhpbvKckoLaGqQA4dT13QgtYuF0IUsZB4od0s3T1Jwd36uFc71h2XaoHmfXWdnwe4i4-cg3QhWyuqRbD7yw-duYDBC0mP2LEqTt7Y_IZBWw=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/X7RzALWF7ZK43Y3ektR3kjgym96KMPJv8x672vEEHBfDGwMrAHL0vmImKd0_VoDZh8vUcyKapVKfYatcLZEZg8ocZZ7KVI7XusGHOKGJopWQxqlMitA6cL3KK2FIgK86fGphrX9s9aVOrJqOyN8P4cFTCnVTNHLmjKMRpeOUrcAo90vMeWOsOqYpe4FBUlYsicEyST_BxHEKd7tU7tTV8DO6I5vbKU4tNDvJEoGjQtbxLH8gGLVydNnYdbiyc08vlu09_PwENKFPj7y1dtu0MmukF1h0jXSuDGm5UskbsIB3nAHb5ynBD5CsCJrXM_4aWEelBkRXVt9J8JRFZubmHaKFDWBU1NJvuAbMf9Y0u87mO6cgc9Rjz5jEVw7xqwT9ezNTSoSNKB_UCc2lh4OiGLs_gwEgE-VrABnlzefMx7-iDoaTyf3c_PWXJFopUWB0FC7OYg2tTeIBU_Dj5QszQauuYKW9R82zO24aW4d5IrOLjCI-N5MqVdmhqB5ah6eO-_NM6uTi5wbmNOXLVK2XvSaSr_l2TsPXRk5I2kKltH_GD77hMrQ0Pg_YuD6tvQdzxTEVs9tasu5nub7rlsKTagmBsyHudrCU-Gll6u7gmQbSs6xocMWfiEmn4gDJ1js10AkgSz_I5ryahSvGihE3JRV8LAb1EOX3sAHNImRW1rqIFkhLAgNubKQwh5P-GMo1kdk7fdClY0DrhaSfm1XAO-08_3jgKiAj_FYEab7RMickK8rKxTPjB2O8ZQvlUe03lMz1s8E2uRDBbBRt2h5nKbBYnpEEbQjpaoY_8_kjUDh0qts49QSEe1pSxlG0GUePvnbB2zNXf2jzWU-bew9brcxOe4gm88DQOBllX0luFGa-Vw8LWm4vrc6Kf1S1-1ew1jOpwxo24CnrgcK0t2DA5h2WzmUt56zncxu7qLG9Oyw=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/p6AHhQY16xjgEjCcV8bZgSIwE_yhrSCDad68FJV10yQPo2VEEchevhspjRiyJutDaf2OXpixxeA-DFtx0smUgYfVq_1UYvE2qb25N9Y4Q7fvWxMMcEzZYns2MoyTi7nrk4xuDgHChJinmACUdc2EcrWv0O_AYpDGHqxMe1qhXA2fHYGDHQEuSgTC5_zgk0_fF4iw9BOTIqcmJNHRzCyvOK-iTXsg7U3qHZ6ddUE5z-ypeHqVHBsNX_pAdeBnG7vpBQ_LPoDmuCtX4nHZFwRrku3MoRqOgxX3UA0SUDvCQGX-r5b1wj1lTUi5AmHrhtrLPTvz0lymbtSYONKnn2g7XOBFL7TGe51YgFuU0NEajHR973zNT5W8pBBM8dBLBNGGHmhGSpGv2gtqJvv_FZRWC_IKa3jlO8YmFZ1H3DGNWcEN5ADnouuQYfgcpXo0kBEfY3z-JqbRl_bPuIdbjXVqqCbliT0Ylgx8b8Qofo6Aok7aitmFND6Pv589HJ_F9mNy8RNqOskvsApgjbBVj2ugWCpCqCBkxU17EkbtmcedpHVwb_ahGSlJSNJKGHBmgvW0OaumjwZrpGiKWjCglpUlMxlD4_5KR0elvUBumN2AFBeWGRUu5wvnTfp20e8b-nYyCIkzkvDNbB6QGN2CCvmv4xEl7X5h_Q-G-eEaKKGnASqqjsnEZM7WF1ePdhrDpmx0htUVCd5VWD1zE4MI_302cyBLH-x5G1QAW43L_7m2Ky_4bV5XeLrtjFji2nN1GyvMLDyrLl04m7j1KsUPHZxHCx3hCiih-4_v8ZSY8o4aliMeJVSSdh_Bsprk2GXVBzIGxe9pg1giP2GtgYNIFBY4K-wudN_U_OB3er5F-6kQs6Y16PkMajkwe_EShYwj_UaJnlcWH3yypSjtbsnMIf2kFitqvtqoL0DF9GDB1Iu0JbA=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/N7HPgFomIhianIfx9MGyaro0NrB_mdH7m4_ySY2BIFdmvzKAU9y1jt0cG8ye4nFLkTi_D2rPc75U5fghKCbG-_8Q5j9ACSKs9vCUEu6VzjFoOnEuozK2yZbvwEWvduiETwdTbDRlDnopf4qSUZbWxos9_cvXB88cvILbdJvEpycXxfI4ibomMsq1yWbO3v2krEpdcyte9UyPqlLs6Gk9I-_3UgHoIGmAeG3qIkO_IB8zXm2v3AdqYGuemNcMNx2A5HZgM34MikHgEp1tBE6g2cvgI8ohP6woklYH6cQglJbsqodNZrSGPSZfktGkn0H162tjEp813W_XyGGRPMVZfBScNSgktkbXzuBeJevMkN0A8l3GTgnX0ZWqa-2dMwihdi8V7qLO9FfaC4c4jrMwFB8GgFLDOOouKOSM0jsoiPu2RurTHfg0fEQGFbkNICXJ40OikBCxcGgh9Ta3zWVO64mKPo1qS0JBxokYZHBZWnCTfAcdwySg8Wej7BGHJNRoeAGrbAFlCMN5PVkpXQjxZG44l1TUaqz53WfchrVuBNKprZcxUNHUxZLWvOW5btkYAhIQNdwZWX7BGFk3cWhliPKQKgMbGztx2Wk9pqKL6ON4XRc1TdDFoJMxhUpPkmS5CqGmM6RsCWt5drpTdl6EHfRiYOnxZC1cOBGdFdLJWSFt7L29Tke1P1UDj3UsA6WSA3U_pbanEEkuDOxFELQeNvMApyZSWhQf4E7qC7ws8GuP1keDTDxUxV_KYIVoqEKVd1mUAfwdwTPEAChRZRCFHcIMQt6PZ5EjJooBZaStv8Hr5j2a14K9N2itxGKr4T05M83Fz-JSjnUNMeGSnvLK42NR2uAuZ_3J00IwyxeaBIll2-BX7xw3ETjo7Fhpift3Y4BjinvbkisVlyFVqmwj8es9qWkdNrxcBXvTUSOYq_w=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/Wrd4nzarsRis5DGPOfmUXRaB92Uy8aCXjHIb_1v19rlFjsWG16TUkpnea7ot9YDG_h3DvtwAGqg_OlJcSAFRQPi0pWzbKPNE1b9fBw6VNz6u8oCrdMsV89XfG91PbJu1rjey2eqmmlQghGjo7Nhn3pb8qFaw6AlyQr0C1WnYbxzmi4qnElI6zBZ8tEH5ndHBmok_bbMopWjq28Q4Grw3_klezAl7FBZ_sMTFGd0zI-_rnWK2j2nKGSHbNYwwBCRIVgz772cSVu0yZyb2OCIxm7NoXm4MgjAcuVnDNC1ivxUvTOV6rsLJQb01YhTAG9ImUnD2TMf0_RImhue8zTOvA4wm1VfyBdUV6eF7k2I15u-DEdeYrnxPUwetmxrq-EQsPLIjucJDjia8_zvjbjsVXA5rnLYdVPYack5tRNjjBv5W96CMHFFohqrb8CcdgpTGLKRLVA-SxzR0DRac91NzBhsTQcV2hMiYgncQuwPO3dD81amdC9RuWihLETvX3QC1MzkvL5ULmFgWYq6uZzfvYdiU2iyOpsyJCQ-1KJnrbEQSRO_PotVRS72j3GWEsim-jXb24agKOXF4PTWw7eaWVBm2IKj9-BY0XRr8IHCCyGgyT0hgcGMfAaZ69x2DS6lg1bXU3scFlie5OeENXsO7pdcq37Ng4qcsRq9hOJrrYgDMvYok12cloSyu_pDsfKriIi4h5xI1tzpE4RJ0pAgeQO9fq_NeUqWkhXPy_2Z-Nu5UIJdGNpNs9if4iVLfdFFI-pPIyuspoCkQKDmc9UTziSKSv0HOXbW9veWzXqXvEE6LlkqM8JFyvLz8Onq0D8V1-INDhwRJ1n66ZvQ3J775QdUWdBW9fMmC-BY5_7m2YpCejUo_ZZ8Rgz_dPliIiYuU0-EVKq0BIoxPVTl5cYBOYwzMfdMk0a5rI58m_ew0gV0=w1200-h640-no?authuser=0");
            listBanner.add("https://lh3.googleusercontent.com/6KwP_PJkdL_9-0Rf8og_inNszdhCsROKlFX97L0LZcp-Bcwrn6j_C6BnbwsN_YOljB6B3SM2FYsseH7YT7Rdk2SvVED285Q_OYk82HSMH9Y6zaLWnDiH13bAkgXyWFtPgurGvhhPsuPiTka70eckxq3FE4rgkQ3VXOp1WOiWDJaVtu4XTxbFtYunuvVX4QsdQWkeQebFsNx4ciK-1AL446LY_lRcOUjMYUNVT1DceINHQONIxmOjVsONhnXEqiUEG44WKNvCE56qD63IeeJTRp4zZ6wAJbF102f_HNsiBtiswEswvjoRt1EsnF0bpw9Dj7GE48bvjKvsoJ7RTTI9sq3IHwUt6qhPBudIUSsxDNQ88OwSAmdE50wYs1rg8hxQLgO0Zqua6ToMHA23yCx3x8qRextwydvs2xNmROls67aHtmh08ynlhqSvGzp8G7PCVv7hkkvgkE85PjOiKcvir2IcW9CBmRIDPC6EqI04pGb4f3erIJ470gR-0JEXgEAhparddHP2smcni2ZWv4y18F8Id6s0mkqPr8E9Pde01nAzhc8TYsNC1sBiVNi3mC4NciWN0JG-k-DP49UlRuUw3gQ5dtDSzCSqMjgPrUxvPfA60ufk8lLAK0RRyBbWAh55RU_au7OycYRpEh3wWDMTFdfcvO-5wIyutcYvGZXRaYjUgb9KQjneZTmeknjVdqJu7cBRKKxGT1_w1MNUObV6EGnVGWlTR3uKeawMEHayO0ZAaH9Z5NQPPCa6PXDpOGwwjiADK8ZRVtnvAzgfVF8Yl7ebURlJ6upDOO1piOabPsnhg5NpUQqGjux6uQRR5cJ2ShndwCv_Cm6PcnOuG_3oraPcCfmAbP4BxZpuQEGLQtFm9cS1zeHaRLiGpfUnVgOogjO1HZa_nu_J41bN3OdQfsC5J5r2a0oiASp0WJk99Ps=w1200-h640-no?authuser=0");

            for (int i = 0; i < listBanner.size(); i++) {
                ImageView imageView = new ImageView(getApplicationContext());
                Glide.with(getApplicationContext()).load(listBanner.get(i)).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                viewFlipper.addView(imageView);
            }*/

            ImageView imageView1 = new ImageView(this);
            imageView1.setImageResource(R.drawable.img_1);
            viewFlipper.addView(imageView1);
            ImageView imageView2 = new ImageView(this);
            imageView2.setImageResource(R.drawable.img_2);
            viewFlipper.addView(imageView2);
            ImageView imageView3 = new ImageView(this);
            imageView3.setImageResource(R.drawable.img_3);
            viewFlipper.addView(imageView3);
            ImageView imageView4 = new ImageView(this);
            imageView4.setImageResource(R.drawable.img_4);
            viewFlipper.addView(imageView4);
            ImageView imageView5 = new ImageView(this);
            imageView5.setImageResource(R.drawable.img_5);
            viewFlipper.addView(imageView5);
            ImageView imageView6 = new ImageView(this);
            imageView6.setImageResource(R.drawable.img_6);
            viewFlipper.addView(imageView6);
            ImageView imageView7 = new ImageView(this);
            imageView7.setImageResource(R.drawable.img_7);
            viewFlipper.addView(imageView7);
            ImageView imageView8 = new ImageView(this);
            imageView8.setImageResource(R.drawable.img_8);
            viewFlipper.addView(imageView8);
            ImageView imageView9 = new ImageView(this);
            imageView9.setImageResource(R.drawable.img_9);
            viewFlipper.addView(imageView9);

            //viewFlipper.setFlipInterval(9000);
            viewFlipper.setAutoStart(true);
            Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
            Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
            viewFlipper.setInAnimation(slide_in);
            viewFlipper.setOutAnimation(slide_out);
        }

        private void Mapping() {
            viewFlipper = findViewById(R.id.viewFlipper);
            gv_categories = (GridView) findViewById(R.id.gv_categories);
            rcv_pd = findViewById(R.id.rcv_pd);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
            rcv_pd.setLayoutManager(layoutManager);
            rcv_pd.setHasFixedSize(true);
            badge = findViewById(R.id.quantity_cart);
            btn_cart = findViewById(R.id.btn_cart);
            btn_user = findViewById(R.id.btn_user);
            searchView = findViewById(R.id.searchView);

            categories = new ArrayList<>();
            products = new ArrayList<>();

            if(Utils.ListCart == null){
                Utils.ListCart = new ArrayList<>();
            }
            else {
                int totalItem = 0;
                for (int i=0; i<Utils.ListCart.size(); i++){
                    totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
                }

                if(totalItem != 0) {
                    badge.setText(String.valueOf(totalItem));
                }
            }

            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.UserCurent.getEmail() == null) {
                        Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intentCart = new Intent(getApplicationContext(), CartActivity.class);
                        startActivity(intentCart);
                    }
                }
            });

            btn_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.UserCurent.getEmail() == null) {
                        Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intentUser = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(intentUser);
                    }
                }
            });

            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSearch = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intentSearch);
                }
            });
        }

        @Override
        protected void onResume() {
            super.onResume();
            int totalItem = 0;
            for (int i=0; i<Utils.ListCart.size(); i++){
                totalItem = totalItem + Utils.ListCart.get(i).getQuantity();
            }

            if(totalItem != 0) {
                badge.setText(String.valueOf(totalItem));
            }
        }

        private boolean isConnected(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((wifi != null && wifi.isConnected()) || (mobi != null && mobi.isConnected())) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            SetBadge();
        }

        @Override
        protected void onDestroy() {
            compositeDisposable.clear();
            super.onDestroy();
        }
    }