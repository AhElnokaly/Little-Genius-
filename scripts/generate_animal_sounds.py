#!/usr/bin/env python3
import math
import os
import random
import struct
import wave

SAMPLE_RATE = 44100

def clamp(v, low=-1.0, high=1.0):
    return max(low, min(high, v))

def save_wav(filepath, samples, target_peak=28500):
    total = len(samples)
    peak = max(abs(s) for s in samples) or 1.0
    scale = target_peak / peak
    fade_len = int(SAMPLE_RATE * 0.02)
    int_samples = []
    for i, s in enumerate(samples):
        v = s * scale
        if i < fade_len:
            v *= (i / float(fade_len))
        elif i > total - fade_len:
            v *= ((total - i) / float(fade_len))
        int_samples.append(int(clamp(v, -32767.0, 32767.0)))

    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    with wave.open(filepath, 'wb') as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(SAMPLE_RATE)
        w.writeframes(struct.pack(f'<{len(int_samples)}h', *int_samples))
    print(f"Generated {filepath:40s} dur={len(int_samples)/SAMPLE_RATE:.2f}s peak={max(abs(x) for x in int_samples)}")

# 1. Turkey (الديك الرومي - قرقرة Gobble gobble)
# Two distinct rapid gobble bursts with pulsating throat flutters descending in pitch
def gen_turkey():
    dur = 1.85
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    def burst(start_t, b_dur, f_start, f_end, n_pulses, amp):
        pulse_len = b_dur / n_pulses
        for p in range(n_pulses):
            p_start = start_t + p * pulse_len
            frac = p / float(n_pulses)
            cur_f = f_start * (1.0 - frac * 0.42)
            n_pulse_samples = int(SAMPLE_RATE * pulse_len)
            phase = 0.0
            for i in range(n_pulse_samples):
                idx = int((p_start * SAMPLE_RATE) + i)
                if idx >= N:
                    break
                p_progress = i / float(n_pulse_samples)
                # Glottal pulse envelope
                env = math.sin(p_progress * math.pi) ** 1.6
                
                # Pitch sweeps within pulse
                f_inst = cur_f * (1.0 - p_progress * 0.12)
                phase += 2.0 * math.pi * f_inst / SAMPLE_RATE

                # Formant resonances (throat / wattle)
                # Formant 1: 520Hz, Formant 2: 1100Hz, Formant 3: 1750Hz
                s0 = math.sin(phase)
                s_sub = math.sin(phase * 0.5) # Guttural rasp
                s_f2 = math.sin(phase * 2.15)
                s_f3 = math.sin(phase * 3.3)
                noise = (random.random() * 2.0 - 1.0) * 0.12

                val = (0.50 * s0 + 0.30 * s_sub + 0.22 * s_f2 + 0.14 * s_f3 + noise) * env * amp
                out[idx] += val

    # Burst 1: Energetic gobble
    burst(start_t=0.06, b_dur=0.68, f_start=880.0, f_end=510.0, n_pulses=17, amp=0.92)
    # Throat breathing / chuckle between bursts
    for i in range(int(0.74 * SAMPLE_RATE), int(0.86 * SAMPLE_RATE)):
        t = (i - 0.74 * SAMPLE_RATE) / SAMPLE_RATE
        out[i] += (math.sin(2.0 * math.pi * 320.0 * t) * 0.18 + (random.random() - 0.5) * 0.10) * math.sin(t / 0.12 * math.pi)
    # Burst 2: Follow-up gobble
    burst(start_t=0.86, b_dur=0.62, f_start=760.0, f_end=430.0, n_pulses=14, amp=0.85)

    return out

# 2. Monkey (القرد - أو أو ها ها / Playful chimp & monkey chatter)
def gen_monkey():
    dur = 2.1
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    # Part 1: Two rising hoots: "Oo! Oo!"
    hoots = [(0.08, 0.26, 360.0, 520.0), (0.40, 0.28, 380.0, 560.0)]
    for start_t, h_dur, f0, f1 in hoots:
        h_samples = int(SAMPLE_RATE * h_dur)
        phase = 0.0
        for i in range(h_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(h_samples)
            f = f0 + (f1 - f0) * math.sin(p * math.pi * 0.5)
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.3
            val = (math.sin(phase) + 0.4 * math.sin(phase * 2) + 0.2 * math.sin(phase * 3)) * env * 0.8
            out[idx] += val

    # Part 2: Excited monkey chatter laughter: "He-he-ha-ha-he-he!"
    laughs = [
        (0.75, 0.12, 680.0), (0.90, 0.12, 740.0),
        (1.05, 0.13, 820.0), (1.21, 0.13, 800.0),
        (1.37, 0.12, 730.0), (1.52, 0.14, 690.0),
        (1.70, 0.22, 920.0) # High excited squeal
    ]
    for start_t, l_dur, freq in laughs:
        l_samples = int(SAMPLE_RATE * l_dur)
        phase = 0.0
        for i in range(l_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(l_samples)
            vibrato = 1.0 + 0.06 * math.sin(2.0 * math.pi * 14.0 * (i / SAMPLE_RATE))
            f = freq * vibrato
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.4
            noise = (random.random() - 0.5) * 0.12
            val = (math.sin(phase) + 0.45 * math.sin(phase * 2.0) + 0.25 * math.sin(phase * 3.0) + noise) * env * 0.95
            out[idx] += val

    return out

# 3. Mouse (الفأر - صرير الفأر / Squeak squeak squeak)
def gen_mouse():
    dur = 1.5
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    squeaks = [
        (0.10, 0.22, 3200.0, 3650.0, 3100.0),
        (0.45, 0.26, 3100.0, 3850.0, 3250.0),
        (0.85, 0.14, 3400.0, 3800.0, 3450.0),
        (1.03, 0.18, 3300.0, 3700.0, 3150.0),
    ]
    for start_t, s_dur, f_start, f_mid, f_end in squeaks:
        s_samples = int(SAMPLE_RATE * s_dur)
        phase = 0.0
        for i in range(s_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(s_samples)
            # Arch pitch trajectory
            f = f_start + (f_mid - f_start) * math.sin(p * math.pi) if p < 0.5 else f_mid + (f_end - f_mid) * ((p - 0.5) / 0.5)
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.2
            # Sweet high harmonic
            val = (math.sin(phase) + 0.25 * math.sin(phase * 2.0)) * env * 0.88
            out[idx] += val

    return out

# 4. Duck (البطة - واك واك / Quack Quack Quack)
def gen_duck():
    dur = 1.75
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    quacks = [
        (0.10, 0.38, 360.0, 0.95),
        (0.60, 0.35, 340.0, 0.88),
        (1.10, 0.32, 310.0, 0.78),
    ]
    for start_t, q_dur, base_f, amp in quacks:
        q_samples = int(SAMPLE_RATE * q_dur)
        phase = 0.0
        for i in range(q_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(q_samples)
            # Duck nasal pitch decay
            f = base_f * (1.1 - 0.22 * p)
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.3
            # Duck bill harmonic saturation (nasal formants at 700Hz, 1400Hz, 2200Hz)
            harmonics = (math.sin(phase) +
                         0.65 * math.sin(phase * 2.0) +
                         0.45 * math.sin(phase * 3.0) +
                         0.30 * math.sin(phase * 4.0) +
                         0.15 * math.sin(phase * 6.0))
            # Beak distortion
            dist = math.tanh(harmonics * 1.5)
            out[idx] += dist * env * amp

    return out

# 5. Cat (القطة - مياو / Meow)
def gen_cat():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    start_idx = int(SAMPLE_RATE * 0.1)
    call_len = int(SAMPLE_RATE * 1.55)
    phase = 0.0
    for i in range(call_len):
        idx = start_idx + i
        if idx >= N: break
        p = i / float(call_len)
        # Pitch: starts ~270Hz (M), rises to ~560Hz (Eee), drops to ~310Hz (Ow)
        if p < 0.25:
            f = 270.0 + (560.0 - 270.0) * (p / 0.25)
        elif p < 0.65:
            f = 560.0 + 30.0 * math.sin((p - 0.25) / 0.4 * math.pi)
        else:
            f = 590.0 - (590.0 - 310.0) * ((p - 0.65) / 0.35)
        
        # Purr vibrato
        f *= (1.0 + 0.02 * math.sin(2.0 * math.pi * 22.0 * (i / SAMPLE_RATE)))
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.35
        # Soft vocal formants
        val = (math.sin(phase) + 0.40 * math.sin(phase * 2.0) + 0.18 * math.sin(phase * 3.0) + 0.08 * math.sin(phase * 4.0)) * env
        out[idx] += val

    return out

# 6. Dog (الكلب - هو هو / Woof Woof)
def gen_dog():
    dur = 1.75
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    barks = [(0.12, 0.36, 175.0, 0.95), (0.65, 0.38, 160.0, 0.90)]
    for start_t, b_dur, f0, amp in barks:
        b_samples = int(SAMPLE_RATE * b_dur)
        phase = 0.0
        for i in range(b_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(b_samples)
            # Bark attack: quick pitch drop from chest
            f = f0 * (1.35 - 0.45 * p)
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.8
            # Chest & muzzle resonance
            chest = math.sin(phase) + 0.55 * math.sin(phase * 2.0) + 0.35 * math.sin(phase * 3.0)
            rasp = (random.random() - 0.5) * 0.18
            val = math.tanh((chest + rasp) * 1.8) * env * amp
            out[idx] += val

    return out

# 7. Cow (البقرة - موو / Mooo)
def gen_cow():
    dur = 2.2
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    start_idx = int(SAMPLE_RATE * 0.1)
    call_len = int(SAMPLE_RATE * 1.95)
    phase = 0.0
    for i in range(call_len):
        idx = start_idx + i
        if idx >= N: break
        p = i / float(call_len)
        # Deep cow moo pitch
        f = 120.0 + 15.0 * math.sin(p * math.pi)
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.2
        # Rich deep harmonics
        val = (math.sin(phase) +
               0.65 * math.sin(phase * 2.0) +
               0.45 * math.sin(phase * 3.0) +
               0.30 * math.sin(phase * 4.0) +
               0.15 * math.sin(phase * 5.0)) * env
        out[idx] += val

    return out

# 8. Sheep (الخروف - باع باع / Baaa-aaa)
def gen_sheep():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    start_idx = int(SAMPLE_RATE * 0.12)
    call_len = int(SAMPLE_RATE * 1.55)
    phase = 0.0
    for i in range(call_len):
        idx = start_idx + i
        if idx >= N: break
        p = i / float(call_len)
        # Sheep tremolo (amplitude and pitch flutter at 9 Hz)
        flutter = 1.0 + 0.04 * math.sin(2.0 * math.pi * 9.5 * (i / SAMPLE_RATE))
        f = (260.0 + 15.0 * math.sin(p * math.pi)) * flutter
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        tremolo_amp = 0.85 + 0.15 * math.sin(2.0 * math.pi * 9.5 * (i / SAMPLE_RATE))
        env = (math.sin(p * math.pi) ** 1.3) * tremolo_amp
        # Open mouth nasal vowel
        val = (math.sin(phase) + 0.55 * math.sin(phase * 2.0) + 0.35 * math.sin(phase * 3.0) + 0.20 * math.sin(phase * 4.0)) * env
        out[idx] += val

    return out

# 9. Rooster (الديك - كوكو كوكو / Cock-a-doodle-doo)
def gen_rooster():
    dur = 2.2
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    # 4 distinct notes: short low, short mid, high punch, long triumph
    notes = [
        (0.08, 0.22, 340.0),
        (0.33, 0.24, 460.0),
        (0.60, 0.28, 580.0),
        (0.92, 1.15, 720.0), # Holding long crowning note
    ]
    for start_t, n_dur, f0 in notes:
        n_samples = int(SAMPLE_RATE * n_dur)
        phase = 0.0
        for i in range(n_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(n_samples)
            f = f0 * (1.0 - 0.18 * (p ** 1.8))
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.2
            # Bright rooster crow harmonics & beak rasp
            rasp = (random.random() - 0.5) * 0.08
            tone = (math.sin(phase) + 0.6 * math.sin(phase * 2.0) + 0.35 * math.sin(phase * 3.0) + 0.2 * math.sin(phase * 4.0) + rasp)
            val = math.tanh(tone * 1.5) * env
            out[idx] += val

    return out

# 10. Lion (الأسد - زئير / Roar)
def gen_lion():
    dur = 2.3
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    call_len = int(SAMPLE_RATE * 2.1)
    phase = 0.0
    for i in range(call_len):
        idx = int(0.08 * SAMPLE_RATE) + i
        if idx >= N: break
        p = i / float(call_len)
        # Deep guttural growl swelling into roar
        f = 75.0 + 40.0 * math.sin(p * math.pi)
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.4
        # Heavy sub-bass and chest saturation
        sub = math.sin(phase * 0.5)
        harm = math.sin(phase) + 0.7 * math.sin(phase * 2.0) + 0.5 * math.sin(phase * 3.0) + 0.3 * math.sin(phase * 4.0)
        growl_noise = (random.random() - 0.5) * 0.25
        val = math.tanh((harm + sub * 0.5 + growl_noise) * 2.2) * env
        out[idx] += val

    return out

# 11. Elephant (الفيل - نهيم الفيل / Trumpet)
def gen_elephant():
    dur = 2.0
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    call_len = int(SAMPLE_RATE * 1.7)
    phase = 0.0
    for i in range(call_len):
        idx = int(0.12 * SAMPLE_RATE) + i
        if idx >= N: break
        p = i / float(call_len)
        # Bright trunk fanfare (pitch curves 420Hz -> 620Hz -> 480Hz)
        f = 440.0 + 160.0 * math.sin(p * math.pi)
        # Trunk lip buzz vibrato (18 Hz)
        f *= (1.0 + 0.05 * math.sin(2.0 * math.pi * 18.0 * (i / SAMPLE_RATE)))
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.3
        # Brassy trumpet overtones
        brass = (math.sin(phase) + 0.7 * math.sin(phase * 2.0) + 0.5 * math.sin(phase * 3.0) + 0.35 * math.sin(phase * 4.0) + 0.2 * math.sin(phase * 5.0))
        val = math.tanh(brass * 1.8) * env
        out[idx] += val

    return out

# 12. Horse (الحصان - صهيل / Neigh-Whinny)
def gen_horse():
    dur = 2.1
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    # Part 1: High fluttering whinny (0.1 to 1.1s)
    whinny_len = int(SAMPLE_RATE * 1.0)
    phase = 0.0
    for i in range(whinny_len):
        idx = int(0.1 * SAMPLE_RATE) + i
        p = i / float(whinny_len)
        trem = math.sin(2.0 * math.pi * 12.0 * (i / SAMPLE_RATE))
        f = (920.0 + 160.0 * math.sin(p * math.pi)) * (1.0 + 0.06 * trem)
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.3
        val = (math.sin(phase) + 0.4 * math.sin(phase * 2.0) + 0.2 * math.sin(phase * 3.0)) * env
        out[idx] += val

    # Part 2: Rhythmic warm chest nickers (1.15 to 1.9s)
    nickers = [(1.15, 0.18), (1.36, 0.16), (1.55, 0.15), (1.72, 0.18)]
    for start_t, n_dur in nickers:
        n_samples = int(SAMPLE_RATE * n_dur)
        p_phase = 0.0
        for i in range(n_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(n_samples)
            f = 220.0 * (1.0 - 0.2 * p)
            p_phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.4
            noise = (random.random() - 0.5) * 0.15
            val = (math.sin(p_phase) + 0.4 * math.sin(p_phase * 2.0) + noise) * env * 0.8
            out[idx] += val

    return out

# 13. Wolf (الذئب - عواء / Howl)
def gen_wolf():
    dur = 2.4
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    call_len = int(SAMPLE_RATE * 2.15)
    phase = 0.0
    for i in range(call_len):
        idx = int(0.1 * SAMPLE_RATE) + i
        if idx >= N: break
        p = i / float(call_len)
        # Low haunting rise to high poignant crest and gentle descent
        if p < 0.35:
            f = 260.0 + (540.0 - 260.0) * (p / 0.35)
        elif p < 0.70:
            f = 540.0 + 20.0 * math.sin((p - 0.35) / 0.35 * math.pi)
        else:
            f = 540.0 - (540.0 - 220.0) * ((p - 0.70) / 0.30)
        
        vib = 1.0 + 0.018 * math.sin(2.0 * math.pi * 5.5 * (i / SAMPLE_RATE))
        phase += 2.0 * math.pi * (f * vib) / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.25
        val = (math.sin(phase) + 0.35 * math.sin(phase * 2.0) + 0.15 * math.sin(phase * 3.0)) * env
        out[idx] += val

    return out

# 14. Bird (العصفور - تغريد / Birdsong Trill)
def gen_bird():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    trills = [
        (0.10, 0.18, 2800.0, 3600.0),
        (0.32, 0.20, 3200.0, 4200.0),
        (0.56, 0.15, 3400.0, 3800.0),
        (0.75, 0.22, 3100.0, 4100.0),
        (1.02, 0.16, 3500.0, 3900.0),
        (1.22, 0.25, 2900.0, 3800.0),
    ]
    for start_t, t_dur, f0, f1 in trills:
        t_samples = int(SAMPLE_RATE * t_dur)
        phase = 0.0
        for i in range(t_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(t_samples)
            # Rapid canary frequency modulation
            chirp_mod = math.sin(2.0 * math.pi * 18.0 * (i / SAMPLE_RATE))
            f = (f0 + (f1 - f0) * p) + 150.0 * chirp_mod
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.3
            val = (math.sin(phase) + 0.2 * math.sin(phase * 2.0)) * env * 0.9
            out[idx] += val

    return out

# 15. Frog (الضفدع - نقيق / Croak Ribbit)
def gen_frog():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    croaks = [(0.15, 0.45, 230.0, 420.0), (0.75, 0.48, 220.0, 410.0)]
    for start_t, c_dur, f_start, f_peak in croaks:
        c_samples = int(SAMPLE_RATE * c_dur)
        phase = 0.0
        for i in range(c_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(c_samples)
            # Dual click croak: quick hop from low throat to vocal sac resonance
            f = f_start + (f_peak - f_start) * (p ** 1.5)
            # Rapid vocal sac flutter (30 Hz)
            am = 0.7 + 0.3 * math.sin(2.0 * math.pi * 32.0 * (i / SAMPLE_RATE))
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = (math.sin(p * math.pi) ** 1.5) * am
            harm = math.sin(phase) + 0.55 * math.sin(phase * 2.0) + 0.35 * math.sin(phase * 3.0)
            val = math.tanh(harm * 1.8) * env
            out[idx] += val

    return out

# 16. Owl (البومة - هو هو / Hoot Hoot)
def gen_owl():
    dur = 2.0
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    hoots = [
        (0.12, 0.42, 340.0),
        (0.68, 0.28, 360.0),
        (1.02, 0.55, 330.0)
    ]
    for start_t, h_dur, f0 in hoots:
        h_samples = int(SAMPLE_RATE * h_dur)
        phase = 0.0
        for i in range(h_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(h_samples)
            f = f0 * (1.0 + 0.04 * math.sin(p * math.pi))
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.2
            # Flute-like hollow tone with subtle breath
            val = (math.sin(phase) + 0.15 * math.sin(phase * 2.0) + (random.random() - 0.5) * 0.03) * env
            out[idx] += val

    return out

# 17. Bee (النحلة - بززز / Buzz)
def gen_bee():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    call_len = int(SAMPLE_RATE * 1.6)
    phase = 0.0
    for i in range(call_len):
        idx = int(0.1 * SAMPLE_RATE) + i
        if idx >= N: break
        p = i / float(call_len)
        # Flyby Doppler pitch modulation (starts 220Hz, peaks 240Hz, drops to 195Hz)
        f = 215.0 + 25.0 * math.sin(p * math.pi)
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = math.sin(p * math.pi) ** 1.2
        # Rich sawtooth harmonic buzz
        buzz = 0.0
        for k in range(1, 9):
            buzz += (1.0 / k) * math.sin(phase * k)
        val = buzz * env * 0.55
        out[idx] += val

    return out

# 18. Dolphin (الدلفين - صفير الدلفين / Dolphin Whistle & Clicks)
def gen_dolphin():
    dur = 2.0
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    # Part 1: Echolocation clicks (0.05 to 0.45s)
    for c in range(12):
        c_time = 0.06 + c * 0.03
        c_idx = int(c_time * SAMPLE_RATE)
        for i in range(int(0.005 * SAMPLE_RATE)):
            if c_idx + i < N:
                out[c_idx + i] += (random.random() * 2.0 - 1.0) * math.sin(i / (0.005 * SAMPLE_RATE) * math.pi) * 0.65

    # Part 2: Playful rising & falling pure whistles
    whistles = [
        (0.50, 0.45, 2100.0, 3200.0, 2400.0),
        (1.05, 0.55, 2300.0, 3400.0, 2200.0)
    ]
    for start_t, w_dur, f0, f_mid, f_end in whistles:
        w_samples = int(SAMPLE_RATE * w_dur)
        phase = 0.0
        for i in range(w_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(w_samples)
            f = f0 + (f_mid - f0) * math.sin(p * math.pi) if p < 0.5 else f_mid + (f_end - f_mid) * ((p - 0.5) / 0.5)
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.3
            val = (math.sin(phase) + 0.12 * math.sin(phase * 2.0)) * env * 0.85
            out[idx] += val

    return out

# 19. Goat (الماعز - ماع ماع / Billy Goat Bleat)
def gen_goat():
    dur = 1.7
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    call_len = int(SAMPLE_RATE * 1.45)
    phase = 0.0
    for i in range(call_len):
        idx = int(0.12 * SAMPLE_RATE) + i
        if idx >= N: break
        p = i / float(call_len)
        # Fast comic 13 Hz vibrato
        trem = math.sin(2.0 * math.pi * 13.5 * (i / SAMPLE_RATE))
        f = (330.0 + 20.0 * math.sin(p * math.pi)) * (1.0 + 0.05 * trem)
        phase += 2.0 * math.pi * f / SAMPLE_RATE
        env = (math.sin(p * math.pi) ** 1.3) * (0.8 + 0.2 * trem)
        val = (math.sin(phase) + 0.6 * math.sin(phase * 2.0) + 0.35 * math.sin(phase * 3.0) + 0.2 * math.sin(phase * 4.0)) * env
        out[idx] += val

    return out

# 20. Penguin (البطريق - صياح البطريق / Penguin Honk)
def gen_penguin():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    honks = [(0.10, 0.40, 330.0), (0.60, 0.42, 350.0), (1.12, 0.38, 310.0)]
    for start_t, h_dur, f0 in honks:
        h_samples = int(SAMPLE_RATE * h_dur)
        phase = 0.0
        for i in range(h_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(h_samples)
            f = f0 * (1.1 - 0.2 * p)
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.4
            # Dual tone braying with rasp
            tone = math.sin(phase) + 0.7 * math.sin(phase * 1.45) + 0.4 * math.sin(phase * 2.2)
            rasp = (random.random() - 0.5) * 0.15
            val = math.tanh((tone + rasp) * 1.7) * env
            out[idx] += val

    return out

# 21. Chicken (الدجاجة - بق بق / Hen Cluck)
def gen_chicken():
    dur = 1.8
    N = int(SAMPLE_RATE * dur)
    out = [0.0] * N

    # Barnyard hen: staccato clucks followed by loud bagawk
    clucks = [
        (0.10, 0.14, 480.0, 0.8),
        (0.32, 0.15, 510.0, 0.85),
        (0.55, 0.14, 470.0, 0.8),
        (0.85, 0.45, 620.0, 0.95), # Loud final bagawk!
    ]
    for start_t, c_dur, f0, amp in clucks:
        c_samples = int(SAMPLE_RATE * c_dur)
        phase = 0.0
        for i in range(c_samples):
            idx = int(start_t * SAMPLE_RATE) + i
            if idx >= N: break
            p = i / float(c_samples)
            f = f0 * (1.2 - 0.4 * (p ** 1.5))
            phase += 2.0 * math.pi * f / SAMPLE_RATE
            env = math.sin(p * math.pi) ** 1.4
            rasp = (random.random() - 0.5) * 0.12
            val = (math.sin(phase) + 0.55 * math.sin(phase * 2.0) + 0.3 * math.sin(phase * 3.0) + rasp) * env * amp
            out[idx] += val

    return out

def main():
    target_dir = "app/src/main/res/raw"
    generators = {
        "turkey": gen_turkey,
        "monkey": gen_monkey,
        "mouse": gen_mouse,
        "duck": gen_duck,
        "cat": gen_cat,
        "dog": gen_dog,
        "cow": gen_cow,
        "sheep": gen_sheep,
        "rooster": gen_rooster,
        "lion": gen_lion,
        "elephant": gen_elephant,
        "horse": gen_horse,
        "wolf": gen_wolf,
        "bird": gen_bird,
        "frog": gen_frog,
        "owl": gen_owl,
        "bee": gen_bee,
        "dolphin": gen_dolphin,
        "goat": gen_goat,
        "penguin": gen_penguin,
        "chicken": gen_chicken,
    }

    for name, gen_fn in generators.items():
        samples = gen_fn()
        path = os.path.join(target_dir, f"{name}.wav")
        save_wav(path, samples)

if __name__ == "__main__":
    main()
