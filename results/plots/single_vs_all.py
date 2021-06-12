import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df_main = pd.read_csv('../data/single_vs_all.csv')

for model_ in ['code2vec', 'code2seq', 'ggnn']:
    print(f'Plotting for {model_}...')
    df_model = df_main[df_main['model'] == model_]

    df_single = df_model[df_model['place'] == 'single']
    df_single = df_single.drop('place', axis=1)
    df_single = df_single.rename(columns={'pcp': 'pcp_single'})

    df_all = df_model[df_model['place'] == 'all']
    df_all = df_all.drop('place', axis=1)
    df_all = df_all.rename(columns={'pcp': 'pcp_all'})

    df = pd.merge(df_single, df_all, how='left', on=['model', 'dataset', 'transformation'])
    print(df.head())

    df.set_index(['transformation', 'dataset'])[['pcp_single', 'pcp_all']].plot.bar(rot=55)
    plt.xlabel('(Transformation, Java Dataset)', fontsize=16, labelpad=10)
    plt.ylabel('Change of Prediction (%)', fontsize=16)
    plt.yticks(np.arange(0, 101, 10))
    plt.rc('legend', fontsize=12)
    plt.tick_params(labelsize=13)
    lg = plt.legend()
    lg.get_texts()[0].set_text('Single place transformation')
    lg.get_texts()[1].set_text('All place transformation')
    plt.gcf().subplots_adjust(bottom=0.25)
    plt.savefig('{}_single_vs_all.png'.format(model_), dpi=400)
