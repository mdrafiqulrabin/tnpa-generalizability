import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df_main = pd.read_csv('../data/correct_vs_incorrect.csv')

for model_ in ['code2vec', 'code2seq', 'ggnn']:
    print(f'Plotting for {model_}...')
    df_model = df_main[df_main['model'] == model_]

    df_correct = df_model[df_model['type'] == 'correct']
    df_correct = df_correct.drop('type', axis=1)
    df_correct = df_correct.rename(columns={'pcp': 'pcp_correct'})

    df_incorrect = df_model[df_model['type'] == 'incorrect']
    df_incorrect = df_incorrect.drop('type', axis=1)
    df_incorrect = df_incorrect.rename(columns={'pcp': 'pcp_incorrect'})

    df = pd.merge(df_correct, df_incorrect, how='left', on=['model', 'dataset', 'transformation'])
    print(df.head())

    df.set_index(['transformation', 'dataset'])[['pcp_correct', 'pcp_incorrect']].plot.bar(rot=55)
    plt.xlabel('(Transformation, Java Dataset)', fontsize=16, labelpad=10)
    plt.ylabel('Change of Prediction (%)', fontsize=16)
    plt.yticks(np.arange(0, 101, 10))
    plt.rc('legend', fontsize=12)
    plt.tick_params(labelsize=13)
    lg = plt.legend()
    lg.get_texts()[0].set_text('Correctly predicted method')
    lg.get_texts()[1].set_text('Incorrectly predicted method')
    plt.gcf().subplots_adjust(bottom=0.25)
    plt.savefig('{}_correct_vs_incorrect.png'.format(model_), dpi=400)
