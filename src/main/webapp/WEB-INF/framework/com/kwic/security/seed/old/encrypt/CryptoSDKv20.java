
package com.kwic.security.seed.old.encrypt;

import java.io.*;

/**
*	맵, property 등 로컬에 저장하는 각종 데이터의 암복호화를 담당하는 클래스
*/
public class CryptoSDKv20
{
    //////////////////////////////////////////////////////
    // MD4 hash algorithm                               //
    //////////////////////////////////////////////////////

    public static void MD4hash(byte[] input, int[] digest)
    {
        int len, N;
        len = input.length;
        N = (len % 2 == 0) ? (len * 8) : ((len + 1) * 8);

        int A, B, C, D, AA, BB, CC, DD;
        int[] X = new int[16];
        A = 0x67452301;
        B = 0xefcdab89;
        C = 0x98badcfe;
        D = 0x10325476;

        int i, j;
        for (i = 0; i < N / 16; ++i) {		 
            if (len % 2 == 1) {
                for (j = 0; j < 8; ++j) {
                    X[j] = (input[2 * i] >> (7 - j)) & 0x01;
                    X[8 + j] = 0;
                }
            } else {
                for (j = 0; j < 8; ++j) {
                    X[j] = (input[2 * i] >> (7 - j)) & 0x01;		
                    X[8 + j] = (input[2 * i + 1] >> (7 - j)) & 0x01;
                }
            }	

            AA = A;
            BB = B;
            CC = C;
            DD = D;

            A = LeftRotation((A + f(B, C, D) + X[ 0]),  3);
            D = LeftRotation((D + f(A, B, C) + X[ 1]),  7);
            C = LeftRotation((C + f(D, A, B) + X[ 2]), 11);
            B = LeftRotation((B + f(D, D, A) + X[ 3]), 19);
            A = LeftRotation((A + f(B, C, D) + X[ 4]),  3);
            D = LeftRotation((D + f(A, B, C) + X[ 5]),  7);
            C = LeftRotation((C + f(D, A, B) + X[ 6]), 11);
            B = LeftRotation((B + f(C, D, A) + X[ 7]), 19);
            A = LeftRotation((A + f(B, C, D) + X[ 8]),  3);
            D = LeftRotation((D + f(A, B, C) + X[ 9]),  7);
            C = LeftRotation((C + f(D, A, B) + X[10]), 11);
            B = LeftRotation((B + f(C, D, A) + X[11]), 19);
            A = LeftRotation((A + f(B, C, D) + X[12]),  3);
            D = LeftRotation((D + f(A, B, C) + X[13]),  7);
            C = LeftRotation((C + f(D, A, B) + X[14]), 11);
            B = LeftRotation((B + f(C, D, A) + X[15]), 19);
	 
            A = LeftRotation((A + h(B, C, D) + X[ 0] + 0x6ED9EBA1),  3);
            D = LeftRotation((D + h(A, B, C) + X[ 8] + 0x6ED9EBA1),  5);
            C = LeftRotation((C + h(A, B, D) + X[ 4] + 0x6ED9EBA1),  9);
            B = LeftRotation((B + h(A, C, D) + X[12] + 0x6ED9EBA1), 13);
            A = LeftRotation((A + h(B, C, D) + X[ 2] + 0x6ED9EBA1),  3);
            D = LeftRotation((D + h(A, B, C) + X[10] + 0x6ED9EBA1),  5);
            C = LeftRotation((C + h(A, B, D) + X[ 6] + 0x6ED9EBA1),  9);
            B = LeftRotation((B + h(A, C, D) + X[14] + 0x6ED9EBA1), 13);
            A = LeftRotation((A + h(B, C, D) + X[ 1] + 0x6ED9EBA1),  3);
            D = LeftRotation((D + h(A, B, C) + X[ 9] + 0x6ED9EBA1),  5);
            C = LeftRotation((C + h(A, B, D) + X[ 5] + 0x6ED9EBA1),  9);
            B = LeftRotation((B + h(A, C, D) + X[13] + 0x6ED9EBA1), 13);
            A = LeftRotation((A + h(B, C, D) + X[ 3] + 0x6ED9EBA1),  3);
            D = LeftRotation((D + h(A, B, C) + X[11] + 0x6ED9EBA1),  5);
            C = LeftRotation((C + h(A, B, D) + X[ 7] + 0x6ED9EBA1),  9);
            B = LeftRotation((B + h(A, C, D) + X[15] + 0x6ED9EBA1), 13);

            A = LeftRotation((A + g(B, C, D) + X[ 0] + 0x5A827999),  3);
            D = LeftRotation((D + g(A, B, C) + X[ 4] + 0x5A827999),  5);
            C = LeftRotation((C + g(A, B, D) + X[ 8] + 0x5A827999),  9);
            B = LeftRotation((B + g(A, C, D) + X[12] + 0x5A827999), 13);
            A = LeftRotation((A + g(B, C, D) + X[ 1] + 0x5A827999),  3);
            D = LeftRotation((D + g(A, B, C) + X[ 5] + 0x5A827999),  5);
            C = LeftRotation((C + g(A, B, D) + X[ 9] + 0x5A827999),  9);
            B = LeftRotation((B + g(A, C, D) + X[13] + 0x5A827999), 13);
            A = LeftRotation((A + g(B, C, D) + X[ 2] + 0x5A827999),  3);
            D = LeftRotation((D + g(A, B, C) + X[ 6] + 0x5A827999),  5);
            C = LeftRotation((C + g(A, B, D) + X[10] + 0x5A827999),  9);
            B = LeftRotation((B + g(A, C, D) + X[14] + 0x5A827999), 13);
            A = LeftRotation((A + g(B, C, D) + X[ 3] + 0x5A827999),  3);
            D = LeftRotation((D + g(A, B, C) + X[ 7] + 0x5A827999),  5);
            C = LeftRotation((C + g(A, B, D) + X[11] + 0x5A827999),  9);
            B = LeftRotation((B + g(A, C, D) + X[15] + 0x5A827999), 13);

            A += AA;
            B += BB;
            C += CC;
            D += DD;
        }

        digest[0] = A;
        digest[1] = B;
        digest[2] = C;
        digest[3] = D;
    }

    private static int LeftRotation(int X, int m)
    {
        return (X << m) | (X >>> (32 - m));
    }

    private static int f(int X, int Y, int Z)
    {
        return (X & Y) | ((~X) & Z);
    }

    private static int g(int X, int Y, int Z)
    {
        return (X & Y) | (X & Z) | (Y & Z);
    }

    private static int h(int X, int Y, int Z)
    {
        return X ^ Y ^ Z;
    }

    //////////////////////////////////////////////////////
    // Rijndael AES algorithm                           //
    // with 128-bit block length and 128-bit key length //
    //////////////////////////////////////////////////////

    /*
        Tables for transformations in block encryption:
    */
    private static int[/*256*/] Te0 =
    {
        0xc66363a5, 0xf87c7c84, 0xee777799, 0xf67b7b8d,
        0xfff2f20d, 0xd66b6bbd, 0xde6f6fb1, 0x91c5c554,
        0x60303050, 0x02010103, 0xce6767a9, 0x562b2b7d,
        0xe7fefe19, 0xb5d7d762, 0x4dababe6, 0xec76769a,
        0x8fcaca45, 0x1f82829d, 0x89c9c940, 0xfa7d7d87,
        0xeffafa15, 0xb25959eb, 0x8e4747c9, 0xfbf0f00b,
        0x41adadec, 0xb3d4d467, 0x5fa2a2fd, 0x45afafea,
        0x239c9cbf, 0x53a4a4f7, 0xe4727296, 0x9bc0c05b,
        0x75b7b7c2, 0xe1fdfd1c, 0x3d9393ae, 0x4c26266a,
        0x6c36365a, 0x7e3f3f41, 0xf5f7f702, 0x83cccc4f,
        0x6834345c, 0x51a5a5f4, 0xd1e5e534, 0xf9f1f108,
        0xe2717193, 0xabd8d873, 0x62313153, 0x2a15153f,
        0x0804040c, 0x95c7c752, 0x46232365, 0x9dc3c35e,
        0x30181828, 0x379696a1, 0x0a05050f, 0x2f9a9ab5,
        0x0e070709, 0x24121236, 0x1b80809b, 0xdfe2e23d,
        0xcdebeb26, 0x4e272769, 0x7fb2b2cd, 0xea75759f,
        0x1209091b, 0x1d83839e, 0x582c2c74, 0x341a1a2e,
        0x361b1b2d, 0xdc6e6eb2, 0xb45a5aee, 0x5ba0a0fb,
        0xa45252f6, 0x763b3b4d, 0xb7d6d661, 0x7db3b3ce,
        0x5229297b, 0xdde3e33e, 0x5e2f2f71, 0x13848497,
        0xa65353f5, 0xb9d1d168, 0x00000000, 0xc1eded2c,
        0x40202060, 0xe3fcfc1f, 0x79b1b1c8, 0xb65b5bed,
        0xd46a6abe, 0x8dcbcb46, 0x67bebed9, 0x7239394b,
        0x944a4ade, 0x984c4cd4, 0xb05858e8, 0x85cfcf4a,
        0xbbd0d06b, 0xc5efef2a, 0x4faaaae5, 0xedfbfb16,
        0x864343c5, 0x9a4d4dd7, 0x66333355, 0x11858594,
        0x8a4545cf, 0xe9f9f910, 0x04020206, 0xfe7f7f81,
        0xa05050f0, 0x783c3c44, 0x259f9fba, 0x4ba8a8e3,
        0xa25151f3, 0x5da3a3fe, 0x804040c0, 0x058f8f8a,
        0x3f9292ad, 0x219d9dbc, 0x70383848, 0xf1f5f504,
        0x63bcbcdf, 0x77b6b6c1, 0xafdada75, 0x42212163,
        0x20101030, 0xe5ffff1a, 0xfdf3f30e, 0xbfd2d26d,
        0x81cdcd4c, 0x180c0c14, 0x26131335, 0xc3ecec2f,
        0xbe5f5fe1, 0x359797a2, 0x884444cc, 0x2e171739,
        0x93c4c457, 0x55a7a7f2, 0xfc7e7e82, 0x7a3d3d47,
        0xc86464ac, 0xba5d5de7, 0x3219192b, 0xe6737395,
        0xc06060a0, 0x19818198, 0x9e4f4fd1, 0xa3dcdc7f,
        0x44222266, 0x542a2a7e, 0x3b9090ab, 0x0b888883,
        0x8c4646ca, 0xc7eeee29, 0x6bb8b8d3, 0x2814143c,
        0xa7dede79, 0xbc5e5ee2, 0x160b0b1d, 0xaddbdb76,
        0xdbe0e03b, 0x64323256, 0x743a3a4e, 0x140a0a1e,
        0x924949db, 0x0c06060a, 0x4824246c, 0xb85c5ce4,
        0x9fc2c25d, 0xbdd3d36e, 0x43acacef, 0xc46262a6,
        0x399191a8, 0x319595a4, 0xd3e4e437, 0xf279798b,
        0xd5e7e732, 0x8bc8c843, 0x6e373759, 0xda6d6db7,
        0x018d8d8c, 0xb1d5d564, 0x9c4e4ed2, 0x49a9a9e0,
        0xd86c6cb4, 0xac5656fa, 0xf3f4f407, 0xcfeaea25,
        0xca6565af, 0xf47a7a8e, 0x47aeaee9, 0x10080818,
        0x6fbabad5, 0xf0787888, 0x4a25256f, 0x5c2e2e72,
        0x381c1c24, 0x57a6a6f1, 0x73b4b4c7, 0x97c6c651,
        0xcbe8e823, 0xa1dddd7c, 0xe874749c, 0x3e1f1f21,
        0x964b4bdd, 0x61bdbddc, 0x0d8b8b86, 0x0f8a8a85,
        0xe0707090, 0x7c3e3e42, 0x71b5b5c4, 0xcc6666aa,
        0x904848d8, 0x06030305, 0xf7f6f601, 0x1c0e0e12,
        0xc26161a3, 0x6a35355f, 0xae5757f9, 0x69b9b9d0,
        0x17868691, 0x99c1c158, 0x3a1d1d27, 0x279e9eb9,
        0xd9e1e138, 0xebf8f813, 0x2b9898b3, 0x22111133,
        0xd26969bb, 0xa9d9d970, 0x078e8e89, 0x339494a7,
        0x2d9b9bb6, 0x3c1e1e22, 0x15878792, 0xc9e9e920,
        0x87cece49, 0xaa5555ff, 0x50282878, 0xa5dfdf7a,
        0x038c8c8f, 0x59a1a1f8, 0x09898980, 0x1a0d0d17,
        0x65bfbfda, 0xd7e6e631, 0x844242c6, 0xd06868b8,
        0x824141c3, 0x299999b0, 0x5a2d2d77, 0x1e0f0f11,
        0x7bb0b0cb, 0xa85454fc, 0x6dbbbbd6, 0x2c16163a
    };
    private static int[/*256*/] Te1 =
    {
        0xa5c66363, 0x84f87c7c, 0x99ee7777, 0x8df67b7b,
        0x0dfff2f2, 0xbdd66b6b, 0xb1de6f6f, 0x5491c5c5,
        0x50603030, 0x03020101, 0xa9ce6767, 0x7d562b2b,
        0x19e7fefe, 0x62b5d7d7, 0xe64dabab, 0x9aec7676,
        0x458fcaca, 0x9d1f8282, 0x4089c9c9, 0x87fa7d7d,
        0x15effafa, 0xebb25959, 0xc98e4747, 0x0bfbf0f0,
        0xec41adad, 0x67b3d4d4, 0xfd5fa2a2, 0xea45afaf,
        0xbf239c9c, 0xf753a4a4, 0x96e47272, 0x5b9bc0c0,
        0xc275b7b7, 0x1ce1fdfd, 0xae3d9393, 0x6a4c2626,
        0x5a6c3636, 0x417e3f3f, 0x02f5f7f7, 0x4f83cccc,
        0x5c683434, 0xf451a5a5, 0x34d1e5e5, 0x08f9f1f1,
        0x93e27171, 0x73abd8d8, 0x53623131, 0x3f2a1515,
        0x0c080404, 0x5295c7c7, 0x65462323, 0x5e9dc3c3,
        0x28301818, 0xa1379696, 0x0f0a0505, 0xb52f9a9a,
        0x090e0707, 0x36241212, 0x9b1b8080, 0x3ddfe2e2,
        0x26cdebeb, 0x694e2727, 0xcd7fb2b2, 0x9fea7575,
        0x1b120909, 0x9e1d8383, 0x74582c2c, 0x2e341a1a,
        0x2d361b1b, 0xb2dc6e6e, 0xeeb45a5a, 0xfb5ba0a0,
        0xf6a45252, 0x4d763b3b, 0x61b7d6d6, 0xce7db3b3,
        0x7b522929, 0x3edde3e3, 0x715e2f2f, 0x97138484,
        0xf5a65353, 0x68b9d1d1, 0x00000000, 0x2cc1eded,
        0x60402020, 0x1fe3fcfc, 0xc879b1b1, 0xedb65b5b,
        0xbed46a6a, 0x468dcbcb, 0xd967bebe, 0x4b723939,
        0xde944a4a, 0xd4984c4c, 0xe8b05858, 0x4a85cfcf,
        0x6bbbd0d0, 0x2ac5efef, 0xe54faaaa, 0x16edfbfb,
        0xc5864343, 0xd79a4d4d, 0x55663333, 0x94118585,
        0xcf8a4545, 0x10e9f9f9, 0x06040202, 0x81fe7f7f,
        0xf0a05050, 0x44783c3c, 0xba259f9f, 0xe34ba8a8,
        0xf3a25151, 0xfe5da3a3, 0xc0804040, 0x8a058f8f,
        0xad3f9292, 0xbc219d9d, 0x48703838, 0x04f1f5f5,
        0xdf63bcbc, 0xc177b6b6, 0x75afdada, 0x63422121,
        0x30201010, 0x1ae5ffff, 0x0efdf3f3, 0x6dbfd2d2,
        0x4c81cdcd, 0x14180c0c, 0x35261313, 0x2fc3ecec,
        0xe1be5f5f, 0xa2359797, 0xcc884444, 0x392e1717,
        0x5793c4c4, 0xf255a7a7, 0x82fc7e7e, 0x477a3d3d,
        0xacc86464, 0xe7ba5d5d, 0x2b321919, 0x95e67373,
        0xa0c06060, 0x98198181, 0xd19e4f4f, 0x7fa3dcdc,
        0x66442222, 0x7e542a2a, 0xab3b9090, 0x830b8888,
        0xca8c4646, 0x29c7eeee, 0xd36bb8b8, 0x3c281414,
        0x79a7dede, 0xe2bc5e5e, 0x1d160b0b, 0x76addbdb,
        0x3bdbe0e0, 0x56643232, 0x4e743a3a, 0x1e140a0a,
        0xdb924949, 0x0a0c0606, 0x6c482424, 0xe4b85c5c,
        0x5d9fc2c2, 0x6ebdd3d3, 0xef43acac, 0xa6c46262,
        0xa8399191, 0xa4319595, 0x37d3e4e4, 0x8bf27979,
        0x32d5e7e7, 0x438bc8c8, 0x596e3737, 0xb7da6d6d,
        0x8c018d8d, 0x64b1d5d5, 0xd29c4e4e, 0xe049a9a9,
        0xb4d86c6c, 0xfaac5656, 0x07f3f4f4, 0x25cfeaea,
        0xafca6565, 0x8ef47a7a, 0xe947aeae, 0x18100808,
        0xd56fbaba, 0x88f07878, 0x6f4a2525, 0x725c2e2e,
        0x24381c1c, 0xf157a6a6, 0xc773b4b4, 0x5197c6c6,
        0x23cbe8e8, 0x7ca1dddd, 0x9ce87474, 0x213e1f1f,
        0xdd964b4b, 0xdc61bdbd, 0x860d8b8b, 0x850f8a8a,
        0x90e07070, 0x427c3e3e, 0xc471b5b5, 0xaacc6666,
        0xd8904848, 0x05060303, 0x01f7f6f6, 0x121c0e0e,
        0xa3c26161, 0x5f6a3535, 0xf9ae5757, 0xd069b9b9,
        0x91178686, 0x5899c1c1, 0x273a1d1d, 0xb9279e9e,
        0x38d9e1e1, 0x13ebf8f8, 0xb32b9898, 0x33221111,
        0xbbd26969, 0x70a9d9d9, 0x89078e8e, 0xa7339494,
        0xb62d9b9b, 0x223c1e1e, 0x92158787, 0x20c9e9e9,
        0x4987cece, 0xffaa5555, 0x78502828, 0x7aa5dfdf,
        0x8f038c8c, 0xf859a1a1, 0x80098989, 0x171a0d0d,
        0xda65bfbf, 0x31d7e6e6, 0xc6844242, 0xb8d06868,
        0xc3824141, 0xb0299999, 0x775a2d2d, 0x111e0f0f,
        0xcb7bb0b0, 0xfca85454, 0xd66dbbbb, 0x3a2c1616
    };
    private static int[/*256*/] Te2 =
    {
        0x63a5c663, 0x7c84f87c, 0x7799ee77, 0x7b8df67b,
        0xf20dfff2, 0x6bbdd66b, 0x6fb1de6f, 0xc55491c5,
        0x30506030, 0x01030201, 0x67a9ce67, 0x2b7d562b,
        0xfe19e7fe, 0xd762b5d7, 0xabe64dab, 0x769aec76,
        0xca458fca, 0x829d1f82, 0xc94089c9, 0x7d87fa7d,
        0xfa15effa, 0x59ebb259, 0x47c98e47, 0xf00bfbf0,
        0xadec41ad, 0xd467b3d4, 0xa2fd5fa2, 0xafea45af,
        0x9cbf239c, 0xa4f753a4, 0x7296e472, 0xc05b9bc0,
        0xb7c275b7, 0xfd1ce1fd, 0x93ae3d93, 0x266a4c26,
        0x365a6c36, 0x3f417e3f, 0xf702f5f7, 0xcc4f83cc,
        0x345c6834, 0xa5f451a5, 0xe534d1e5, 0xf108f9f1,
        0x7193e271, 0xd873abd8, 0x31536231, 0x153f2a15,
        0x040c0804, 0xc75295c7, 0x23654623, 0xc35e9dc3,
        0x18283018, 0x96a13796, 0x050f0a05, 0x9ab52f9a,
        0x07090e07, 0x12362412, 0x809b1b80, 0xe23ddfe2,
        0xeb26cdeb, 0x27694e27, 0xb2cd7fb2, 0x759fea75,
        0x091b1209, 0x839e1d83, 0x2c74582c, 0x1a2e341a,
        0x1b2d361b, 0x6eb2dc6e, 0x5aeeb45a, 0xa0fb5ba0,
        0x52f6a452, 0x3b4d763b, 0xd661b7d6, 0xb3ce7db3,
        0x297b5229, 0xe33edde3, 0x2f715e2f, 0x84971384,
        0x53f5a653, 0xd168b9d1, 0x00000000, 0xed2cc1ed,
        0x20604020, 0xfc1fe3fc, 0xb1c879b1, 0x5bedb65b,
        0x6abed46a, 0xcb468dcb, 0xbed967be, 0x394b7239,
        0x4ade944a, 0x4cd4984c, 0x58e8b058, 0xcf4a85cf,
        0xd06bbbd0, 0xef2ac5ef, 0xaae54faa, 0xfb16edfb,
        0x43c58643, 0x4dd79a4d, 0x33556633, 0x85941185,
        0x45cf8a45, 0xf910e9f9, 0x02060402, 0x7f81fe7f,
        0x50f0a050, 0x3c44783c, 0x9fba259f, 0xa8e34ba8,
        0x51f3a251, 0xa3fe5da3, 0x40c08040, 0x8f8a058f,
        0x92ad3f92, 0x9dbc219d, 0x38487038, 0xf504f1f5,
        0xbcdf63bc, 0xb6c177b6, 0xda75afda, 0x21634221,
        0x10302010, 0xff1ae5ff, 0xf30efdf3, 0xd26dbfd2,
        0xcd4c81cd, 0x0c14180c, 0x13352613, 0xec2fc3ec,
        0x5fe1be5f, 0x97a23597, 0x44cc8844, 0x17392e17,
        0xc45793c4, 0xa7f255a7, 0x7e82fc7e, 0x3d477a3d,
        0x64acc864, 0x5de7ba5d, 0x192b3219, 0x7395e673,
        0x60a0c060, 0x81981981, 0x4fd19e4f, 0xdc7fa3dc,
        0x22664422, 0x2a7e542a, 0x90ab3b90, 0x88830b88,
        0x46ca8c46, 0xee29c7ee, 0xb8d36bb8, 0x143c2814,
        0xde79a7de, 0x5ee2bc5e, 0x0b1d160b, 0xdb76addb,
        0xe03bdbe0, 0x32566432, 0x3a4e743a, 0x0a1e140a,
        0x49db9249, 0x060a0c06, 0x246c4824, 0x5ce4b85c,
        0xc25d9fc2, 0xd36ebdd3, 0xacef43ac, 0x62a6c462,
        0x91a83991, 0x95a43195, 0xe437d3e4, 0x798bf279,
        0xe732d5e7, 0xc8438bc8, 0x37596e37, 0x6db7da6d,
        0x8d8c018d, 0xd564b1d5, 0x4ed29c4e, 0xa9e049a9,
        0x6cb4d86c, 0x56faac56, 0xf407f3f4, 0xea25cfea,
        0x65afca65, 0x7a8ef47a, 0xaee947ae, 0x08181008,
        0xbad56fba, 0x7888f078, 0x256f4a25, 0x2e725c2e,
        0x1c24381c, 0xa6f157a6, 0xb4c773b4, 0xc65197c6,
        0xe823cbe8, 0xdd7ca1dd, 0x749ce874, 0x1f213e1f,
        0x4bdd964b, 0xbddc61bd, 0x8b860d8b, 0x8a850f8a,
        0x7090e070, 0x3e427c3e, 0xb5c471b5, 0x66aacc66,
        0x48d89048, 0x03050603, 0xf601f7f6, 0x0e121c0e,
        0x61a3c261, 0x355f6a35, 0x57f9ae57, 0xb9d069b9,
        0x86911786, 0xc15899c1, 0x1d273a1d, 0x9eb9279e,
        0xe138d9e1, 0xf813ebf8, 0x98b32b98, 0x11332211,
        0x69bbd269, 0xd970a9d9, 0x8e89078e, 0x94a73394,
        0x9bb62d9b, 0x1e223c1e, 0x87921587, 0xe920c9e9,
        0xce4987ce, 0x55ffaa55, 0x28785028, 0xdf7aa5df,
        0x8c8f038c, 0xa1f859a1, 0x89800989, 0x0d171a0d,
        0xbfda65bf, 0xe631d7e6, 0x42c68442, 0x68b8d068,
        0x41c38241, 0x99b02999, 0x2d775a2d, 0x0f111e0f,
        0xb0cb7bb0, 0x54fca854, 0xbbd66dbb, 0x163a2c16
    };
    private static int[/*256*/] Te3 =
    {
        0x6363a5c6, 0x7c7c84f8, 0x777799ee, 0x7b7b8df6,
        0xf2f20dff, 0x6b6bbdd6, 0x6f6fb1de, 0xc5c55491,
        0x30305060, 0x01010302, 0x6767a9ce, 0x2b2b7d56,
        0xfefe19e7, 0xd7d762b5, 0xababe64d, 0x76769aec,
        0xcaca458f, 0x82829d1f, 0xc9c94089, 0x7d7d87fa,
        0xfafa15ef, 0x5959ebb2, 0x4747c98e, 0xf0f00bfb,
        0xadadec41, 0xd4d467b3, 0xa2a2fd5f, 0xafafea45,
        0x9c9cbf23, 0xa4a4f753, 0x727296e4, 0xc0c05b9b,
        0xb7b7c275, 0xfdfd1ce1, 0x9393ae3d, 0x26266a4c,
        0x36365a6c, 0x3f3f417e, 0xf7f702f5, 0xcccc4f83,
        0x34345c68, 0xa5a5f451, 0xe5e534d1, 0xf1f108f9,
        0x717193e2, 0xd8d873ab, 0x31315362, 0x15153f2a,
        0x04040c08, 0xc7c75295, 0x23236546, 0xc3c35e9d,
        0x18182830, 0x9696a137, 0x05050f0a, 0x9a9ab52f,
        0x0707090e, 0x12123624, 0x80809b1b, 0xe2e23ddf,
        0xebeb26cd, 0x2727694e, 0xb2b2cd7f, 0x75759fea,
        0x09091b12, 0x83839e1d, 0x2c2c7458, 0x1a1a2e34,
        0x1b1b2d36, 0x6e6eb2dc, 0x5a5aeeb4, 0xa0a0fb5b,
        0x5252f6a4, 0x3b3b4d76, 0xd6d661b7, 0xb3b3ce7d,
        0x29297b52, 0xe3e33edd, 0x2f2f715e, 0x84849713,
        0x5353f5a6, 0xd1d168b9, 0x00000000, 0xeded2cc1,
        0x20206040, 0xfcfc1fe3, 0xb1b1c879, 0x5b5bedb6,
        0x6a6abed4, 0xcbcb468d, 0xbebed967, 0x39394b72,
        0x4a4ade94, 0x4c4cd498, 0x5858e8b0, 0xcfcf4a85,
        0xd0d06bbb, 0xefef2ac5, 0xaaaae54f, 0xfbfb16ed,
        0x4343c586, 0x4d4dd79a, 0x33335566, 0x85859411,
        0x4545cf8a, 0xf9f910e9, 0x02020604, 0x7f7f81fe,
        0x5050f0a0, 0x3c3c4478, 0x9f9fba25, 0xa8a8e34b,
        0x5151f3a2, 0xa3a3fe5d, 0x4040c080, 0x8f8f8a05,
        0x9292ad3f, 0x9d9dbc21, 0x38384870, 0xf5f504f1,
        0xbcbcdf63, 0xb6b6c177, 0xdada75af, 0x21216342,
        0x10103020, 0xffff1ae5, 0xf3f30efd, 0xd2d26dbf,
        0xcdcd4c81, 0x0c0c1418, 0x13133526, 0xecec2fc3,
        0x5f5fe1be, 0x9797a235, 0x4444cc88, 0x1717392e,
        0xc4c45793, 0xa7a7f255, 0x7e7e82fc, 0x3d3d477a,
        0x6464acc8, 0x5d5de7ba, 0x19192b32, 0x737395e6,
        0x6060a0c0, 0x81819819, 0x4f4fd19e, 0xdcdc7fa3,
        0x22226644, 0x2a2a7e54, 0x9090ab3b, 0x8888830b,
        0x4646ca8c, 0xeeee29c7, 0xb8b8d36b, 0x14143c28,
        0xdede79a7, 0x5e5ee2bc, 0x0b0b1d16, 0xdbdb76ad,
        0xe0e03bdb, 0x32325664, 0x3a3a4e74, 0x0a0a1e14,
        0x4949db92, 0x06060a0c, 0x24246c48, 0x5c5ce4b8,
        0xc2c25d9f, 0xd3d36ebd, 0xacacef43, 0x6262a6c4,
        0x9191a839, 0x9595a431, 0xe4e437d3, 0x79798bf2,
        0xe7e732d5, 0xc8c8438b, 0x3737596e, 0x6d6db7da,
        0x8d8d8c01, 0xd5d564b1, 0x4e4ed29c, 0xa9a9e049,
        0x6c6cb4d8, 0x5656faac, 0xf4f407f3, 0xeaea25cf,
        0x6565afca, 0x7a7a8ef4, 0xaeaee947, 0x08081810,
        0xbabad56f, 0x787888f0, 0x25256f4a, 0x2e2e725c,
        0x1c1c2438, 0xa6a6f157, 0xb4b4c773, 0xc6c65197,
        0xe8e823cb, 0xdddd7ca1, 0x74749ce8, 0x1f1f213e,
        0x4b4bdd96, 0xbdbddc61, 0x8b8b860d, 0x8a8a850f,
        0x707090e0, 0x3e3e427c, 0xb5b5c471, 0x6666aacc,
        0x4848d890, 0x03030506, 0xf6f601f7, 0x0e0e121c,
        0x6161a3c2, 0x35355f6a, 0x5757f9ae, 0xb9b9d069,
        0x86869117, 0xc1c15899, 0x1d1d273a, 0x9e9eb927,
        0xe1e138d9, 0xf8f813eb, 0x9898b32b, 0x11113322,
        0x6969bbd2, 0xd9d970a9, 0x8e8e8907, 0x9494a733,
        0x9b9bb62d, 0x1e1e223c, 0x87879215, 0xe9e920c9,
        0xcece4987, 0x5555ffaa, 0x28287850, 0xdfdf7aa5,
        0x8c8c8f03, 0xa1a1f859, 0x89898009, 0x0d0d171a,
        0xbfbfda65, 0xe6e631d7, 0x4242c684, 0x6868b8d0,
        0x4141c382, 0x9999b029, 0x2d2d775a, 0x0f0f111e,
        0xb0b0cb7b, 0x5454fca8, 0xbbbbd66d, 0x16163a2c
    };
    private static int[/*256*/] Te4 =
    {
        0x63636363, 0x7c7c7c7c, 0x77777777, 0x7b7b7b7b,
        0xf2f2f2f2, 0x6b6b6b6b, 0x6f6f6f6f, 0xc5c5c5c5,
        0x30303030, 0x01010101, 0x67676767, 0x2b2b2b2b,
        0xfefefefe, 0xd7d7d7d7, 0xabababab, 0x76767676,
        0xcacacaca, 0x82828282, 0xc9c9c9c9, 0x7d7d7d7d,
        0xfafafafa, 0x59595959, 0x47474747, 0xf0f0f0f0,
        0xadadadad, 0xd4d4d4d4, 0xa2a2a2a2, 0xafafafaf,
        0x9c9c9c9c, 0xa4a4a4a4, 0x72727272, 0xc0c0c0c0,
        0xb7b7b7b7, 0xfdfdfdfd, 0x93939393, 0x26262626,
        0x36363636, 0x3f3f3f3f, 0xf7f7f7f7, 0xcccccccc,
        0x34343434, 0xa5a5a5a5, 0xe5e5e5e5, 0xf1f1f1f1,
        0x71717171, 0xd8d8d8d8, 0x31313131, 0x15151515,
        0x04040404, 0xc7c7c7c7, 0x23232323, 0xc3c3c3c3,
        0x18181818, 0x96969696, 0x05050505, 0x9a9a9a9a,
        0x07070707, 0x12121212, 0x80808080, 0xe2e2e2e2,
        0xebebebeb, 0x27272727, 0xb2b2b2b2, 0x75757575,
        0x09090909, 0x83838383, 0x2c2c2c2c, 0x1a1a1a1a,
        0x1b1b1b1b, 0x6e6e6e6e, 0x5a5a5a5a, 0xa0a0a0a0,
        0x52525252, 0x3b3b3b3b, 0xd6d6d6d6, 0xb3b3b3b3,
        0x29292929, 0xe3e3e3e3, 0x2f2f2f2f, 0x84848484,
        0x53535353, 0xd1d1d1d1, 0x00000000, 0xedededed,
        0x20202020, 0xfcfcfcfc, 0xb1b1b1b1, 0x5b5b5b5b,
        0x6a6a6a6a, 0xcbcbcbcb, 0xbebebebe, 0x39393939,
        0x4a4a4a4a, 0x4c4c4c4c, 0x58585858, 0xcfcfcfcf,
        0xd0d0d0d0, 0xefefefef, 0xaaaaaaaa, 0xfbfbfbfb,
        0x43434343, 0x4d4d4d4d, 0x33333333, 0x85858585,
        0x45454545, 0xf9f9f9f9, 0x02020202, 0x7f7f7f7f,
        0x50505050, 0x3c3c3c3c, 0x9f9f9f9f, 0xa8a8a8a8,
        0x51515151, 0xa3a3a3a3, 0x40404040, 0x8f8f8f8f,
        0x92929292, 0x9d9d9d9d, 0x38383838, 0xf5f5f5f5,
        0xbcbcbcbc, 0xb6b6b6b6, 0xdadadada, 0x21212121,
        0x10101010, 0xffffffff, 0xf3f3f3f3, 0xd2d2d2d2,
        0xcdcdcdcd, 0x0c0c0c0c, 0x13131313, 0xecececec,
        0x5f5f5f5f, 0x97979797, 0x44444444, 0x17171717,
        0xc4c4c4c4, 0xa7a7a7a7, 0x7e7e7e7e, 0x3d3d3d3d,
        0x64646464, 0x5d5d5d5d, 0x19191919, 0x73737373,
        0x60606060, 0x81818181, 0x4f4f4f4f, 0xdcdcdcdc,
        0x22222222, 0x2a2a2a2a, 0x90909090, 0x88888888,
        0x46464646, 0xeeeeeeee, 0xb8b8b8b8, 0x14141414,
        0xdededede, 0x5e5e5e5e, 0x0b0b0b0b, 0xdbdbdbdb,
        0xe0e0e0e0, 0x32323232, 0x3a3a3a3a, 0x0a0a0a0a,
        0x49494949, 0x06060606, 0x24242424, 0x5c5c5c5c,
        0xc2c2c2c2, 0xd3d3d3d3, 0xacacacac, 0x62626262,
        0x91919191, 0x95959595, 0xe4e4e4e4, 0x79797979,
        0xe7e7e7e7, 0xc8c8c8c8, 0x37373737, 0x6d6d6d6d,
        0x8d8d8d8d, 0xd5d5d5d5, 0x4e4e4e4e, 0xa9a9a9a9,
        0x6c6c6c6c, 0x56565656, 0xf4f4f4f4, 0xeaeaeaea,
        0x65656565, 0x7a7a7a7a, 0xaeaeaeae, 0x08080808,
        0xbabababa, 0x78787878, 0x25252525, 0x2e2e2e2e,
        0x1c1c1c1c, 0xa6a6a6a6, 0xb4b4b4b4, 0xc6c6c6c6,
        0xe8e8e8e8, 0xdddddddd, 0x74747474, 0x1f1f1f1f,
        0x4b4b4b4b, 0xbdbdbdbd, 0x8b8b8b8b, 0x8a8a8a8a,
        0x70707070, 0x3e3e3e3e, 0xb5b5b5b5, 0x66666666,
        0x48484848, 0x03030303, 0xf6f6f6f6, 0x0e0e0e0e,
        0x61616161, 0x35353535, 0x57575757, 0xb9b9b9b9,
        0x86868686, 0xc1c1c1c1, 0x1d1d1d1d, 0x9e9e9e9e,
        0xe1e1e1e1, 0xf8f8f8f8, 0x98989898, 0x11111111,
        0x69696969, 0xd9d9d9d9, 0x8e8e8e8e, 0x94949494,
        0x9b9b9b9b, 0x1e1e1e1e, 0x87878787, 0xe9e9e9e9,
        0xcececece, 0x55555555, 0x28282828, 0xdfdfdfdf,
        0x8c8c8c8c, 0xa1a1a1a1, 0x89898989, 0x0d0d0d0d,
        0xbfbfbfbf, 0xe6e6e6e6, 0x42424242, 0x68686868,
        0x41414141, 0x99999999, 0x2d2d2d2d, 0x0f0f0f0f,
        0xb0b0b0b0, 0x54545454, 0xbbbbbbbb, 0x16161616
    };

    /*
        Tables for transformations in block decryption:
    */
    private static int[/*256*/] Td0 =
    {
        0x51f4a750, 0x7e416553, 0x1a17a4c3, 0x3a275e96,
        0x3bab6bcb, 0x1f9d45f1, 0xacfa58ab, 0x4be30393,
        0x2030fa55, 0xad766df6, 0x88cc7691, 0xf5024c25,
        0x4fe5d7fc, 0xc52acbd7, 0x26354480, 0xb562a38f,
        0xdeb15a49, 0x25ba1b67, 0x45ea0e98, 0x5dfec0e1,
        0xc32f7502, 0x814cf012, 0x8d4697a3, 0x6bd3f9c6,
        0x038f5fe7, 0x15929c95, 0xbf6d7aeb, 0x955259da,
        0xd4be832d, 0x587421d3, 0x49e06929, 0x8ec9c844,
        0x75c2896a, 0xf48e7978, 0x99583e6b, 0x27b971dd,
        0xbee14fb6, 0xf088ad17, 0xc920ac66, 0x7dce3ab4,
        0x63df4a18, 0xe51a3182, 0x97513360, 0x62537f45,
        0xb16477e0, 0xbb6bae84, 0xfe81a01c, 0xf9082b94,
        0x70486858, 0x8f45fd19, 0x94de6c87, 0x527bf8b7,
        0xab73d323, 0x724b02e2, 0xe31f8f57, 0x6655ab2a,
        0xb2eb2807, 0x2fb5c203, 0x86c57b9a, 0xd33708a5,
        0x302887f2, 0x23bfa5b2, 0x02036aba, 0xed16825c,
        0x8acf1c2b, 0xa779b492, 0xf307f2f0, 0x4e69e2a1,
        0x65daf4cd, 0x0605bed5, 0xd134621f, 0xc4a6fe8a,
        0x342e539d, 0xa2f355a0, 0x058ae132, 0xa4f6eb75,
        0x0b83ec39, 0x4060efaa, 0x5e719f06, 0xbd6e1051,
        0x3e218af9, 0x96dd063d, 0xdd3e05ae, 0x4de6bd46,
        0x91548db5, 0x71c45d05, 0x0406d46f, 0x605015ff,
        0x1998fb24, 0xd6bde997, 0x894043cc, 0x67d99e77,
        0xb0e842bd, 0x07898b88, 0xe7195b38, 0x79c8eedb,
        0xa17c0a47, 0x7c420fe9, 0xf8841ec9, 0x00000000,
        0x09808683, 0x322bed48, 0x1e1170ac, 0x6c5a724e,
        0xfd0efffb, 0x0f853856, 0x3daed51e, 0x362d3927,
        0x0a0fd964, 0x685ca621, 0x9b5b54d1, 0x24362e3a,
        0x0c0a67b1, 0x9357e70f, 0xb4ee96d2, 0x1b9b919e,
        0x80c0c54f, 0x61dc20a2, 0x5a774b69, 0x1c121a16,
        0xe293ba0a, 0xc0a02ae5, 0x3c22e043, 0x121b171d,
        0x0e090d0b, 0xf28bc7ad, 0x2db6a8b9, 0x141ea9c8,
        0x57f11985, 0xaf75074c, 0xee99ddbb, 0xa37f60fd,
        0xf701269f, 0x5c72f5bc, 0x44663bc5, 0x5bfb7e34,
        0x8b432976, 0xcb23c6dc, 0xb6edfc68, 0xb8e4f163,
        0xd731dcca, 0x42638510, 0x13972240, 0x84c61120,
        0x854a247d, 0xd2bb3df8, 0xaef93211, 0xc729a16d,
        0x1d9e2f4b, 0xdcb230f3, 0x0d8652ec, 0x77c1e3d0,
        0x2bb3166c, 0xa970b999, 0x119448fa, 0x47e96422,
        0xa8fc8cc4, 0xa0f03f1a, 0x567d2cd8, 0x223390ef,
        0x87494ec7, 0xd938d1c1, 0x8ccaa2fe, 0x98d40b36,
        0xa6f581cf, 0xa57ade28, 0xdab78e26, 0x3fadbfa4,
        0x2c3a9de4, 0x5078920d, 0x6a5fcc9b, 0x547e4662,
        0xf68d13c2, 0x90d8b8e8, 0x2e39f75e, 0x82c3aff5,
        0x9f5d80be, 0x69d0937c, 0x6fd52da9, 0xcf2512b3,
        0xc8ac993b, 0x10187da7, 0xe89c636e, 0xdb3bbb7b,
        0xcd267809, 0x6e5918f4, 0xec9ab701, 0x834f9aa8,
        0xe6956e65, 0xaaffe67e, 0x21bccf08, 0xef15e8e6,
        0xbae79bd9, 0x4a6f36ce, 0xea9f09d4, 0x29b07cd6,
        0x31a4b2af, 0x2a3f2331, 0xc6a59430, 0x35a266c0,
        0x744ebc37, 0xfc82caa6, 0xe090d0b0, 0x33a7d815,
        0xf104984a, 0x41ecdaf7, 0x7fcd500e, 0x1791f62f,
        0x764dd68d, 0x43efb04d, 0xccaa4d54, 0xe49604df,
        0x9ed1b5e3, 0x4c6a881b, 0xc12c1fb8, 0x4665517f,
        0x9d5eea04, 0x018c355d, 0xfa877473, 0xfb0b412e,
        0xb3671d5a, 0x92dbd252, 0xe9105633, 0x6dd64713,
        0x9ad7618c, 0x37a10c7a, 0x59f8148e, 0xeb133c89,
        0xcea927ee, 0xb761c935, 0xe11ce5ed, 0x7a47b13c,
        0x9cd2df59, 0x55f2733f, 0x1814ce79, 0x73c737bf,
        0x53f7cdea, 0x5ffdaa5b, 0xdf3d6f14, 0x7844db86,
        0xcaaff381, 0xb968c43e, 0x3824342c, 0xc2a3405f,
        0x161dc372, 0xbce2250c, 0x283c498b, 0xff0d9541,
        0x39a80171, 0x080cb3de, 0xd8b4e49c, 0x6456c190,
        0x7bcb8461, 0xd532b670, 0x486c5c74, 0xd0b85742
    };
    private static int[/*256*/] Td1 =
    {
        0x5051f4a7, 0x537e4165, 0xc31a17a4, 0x963a275e,
        0xcb3bab6b, 0xf11f9d45, 0xabacfa58, 0x934be303,
        0x552030fa, 0xf6ad766d, 0x9188cc76, 0x25f5024c,
        0xfc4fe5d7, 0xd7c52acb, 0x80263544, 0x8fb562a3,
        0x49deb15a, 0x6725ba1b, 0x9845ea0e, 0xe15dfec0,
        0x02c32f75, 0x12814cf0, 0xa38d4697, 0xc66bd3f9,
        0xe7038f5f, 0x9515929c, 0xebbf6d7a, 0xda955259,
        0x2dd4be83, 0xd3587421, 0x2949e069, 0x448ec9c8,
        0x6a75c289, 0x78f48e79, 0x6b99583e, 0xdd27b971,
        0xb6bee14f, 0x17f088ad, 0x66c920ac, 0xb47dce3a,
        0x1863df4a, 0x82e51a31, 0x60975133, 0x4562537f,
        0xe0b16477, 0x84bb6bae, 0x1cfe81a0, 0x94f9082b,
        0x58704868, 0x198f45fd, 0x8794de6c, 0xb7527bf8,
        0x23ab73d3, 0xe2724b02, 0x57e31f8f, 0x2a6655ab,
        0x07b2eb28, 0x032fb5c2, 0x9a86c57b, 0xa5d33708,
        0xf2302887, 0xb223bfa5, 0xba02036a, 0x5ced1682,
        0x2b8acf1c, 0x92a779b4, 0xf0f307f2, 0xa14e69e2,
        0xcd65daf4, 0xd50605be, 0x1fd13462, 0x8ac4a6fe,
        0x9d342e53, 0xa0a2f355, 0x32058ae1, 0x75a4f6eb,
        0x390b83ec, 0xaa4060ef, 0x065e719f, 0x51bd6e10,
        0xf93e218a, 0x3d96dd06, 0xaedd3e05, 0x464de6bd,
        0xb591548d, 0x0571c45d, 0x6f0406d4, 0xff605015,
        0x241998fb, 0x97d6bde9, 0xcc894043, 0x7767d99e,
        0xbdb0e842, 0x8807898b, 0x38e7195b, 0xdb79c8ee,
        0x47a17c0a, 0xe97c420f, 0xc9f8841e, 0x00000000,
        0x83098086, 0x48322bed, 0xac1e1170, 0x4e6c5a72,
        0xfbfd0eff, 0x560f8538, 0x1e3daed5, 0x27362d39,
        0x640a0fd9, 0x21685ca6, 0xd19b5b54, 0x3a24362e,
        0xb10c0a67, 0x0f9357e7, 0xd2b4ee96, 0x9e1b9b91,
        0x4f80c0c5, 0xa261dc20, 0x695a774b, 0x161c121a,
        0x0ae293ba, 0xe5c0a02a, 0x433c22e0, 0x1d121b17,
        0x0b0e090d, 0xadf28bc7, 0xb92db6a8, 0xc8141ea9,
        0x8557f119, 0x4caf7507, 0xbbee99dd, 0xfda37f60,
        0x9ff70126, 0xbc5c72f5, 0xc544663b, 0x345bfb7e,
        0x768b4329, 0xdccb23c6, 0x68b6edfc, 0x63b8e4f1,
        0xcad731dc, 0x10426385, 0x40139722, 0x2084c611,
        0x7d854a24, 0xf8d2bb3d, 0x11aef932, 0x6dc729a1,
        0x4b1d9e2f, 0xf3dcb230, 0xec0d8652, 0xd077c1e3,
        0x6c2bb316, 0x99a970b9, 0xfa119448, 0x2247e964,
        0xc4a8fc8c, 0x1aa0f03f, 0xd8567d2c, 0xef223390,
        0xc787494e, 0xc1d938d1, 0xfe8ccaa2, 0x3698d40b,
        0xcfa6f581, 0x28a57ade, 0x26dab78e, 0xa43fadbf,
        0xe42c3a9d, 0x0d507892, 0x9b6a5fcc, 0x62547e46,
        0xc2f68d13, 0xe890d8b8, 0x5e2e39f7, 0xf582c3af,
        0xbe9f5d80, 0x7c69d093, 0xa96fd52d, 0xb3cf2512,
        0x3bc8ac99, 0xa710187d, 0x6ee89c63, 0x7bdb3bbb,
        0x09cd2678, 0xf46e5918, 0x01ec9ab7, 0xa8834f9a,
        0x65e6956e, 0x7eaaffe6, 0x0821bccf, 0xe6ef15e8,
        0xd9bae79b, 0xce4a6f36, 0xd4ea9f09, 0xd629b07c,
        0xaf31a4b2, 0x312a3f23, 0x30c6a594, 0xc035a266,
        0x37744ebc, 0xa6fc82ca, 0xb0e090d0, 0x1533a7d8,
        0x4af10498, 0xf741ecda, 0x0e7fcd50, 0x2f1791f6,
        0x8d764dd6, 0x4d43efb0, 0x54ccaa4d, 0xdfe49604,
        0xe39ed1b5, 0x1b4c6a88, 0xb8c12c1f, 0x7f466551,
        0x049d5eea, 0x5d018c35, 0x73fa8774, 0x2efb0b41,
        0x5ab3671d, 0x5292dbd2, 0x33e91056, 0x136dd647,
        0x8c9ad761, 0x7a37a10c, 0x8e59f814, 0x89eb133c,
        0xeecea927, 0x35b761c9, 0xede11ce5, 0x3c7a47b1,
        0x599cd2df, 0x3f55f273, 0x791814ce, 0xbf73c737,
        0xea53f7cd, 0x5b5ffdaa, 0x14df3d6f, 0x867844db,
        0x81caaff3, 0x3eb968c4, 0x2c382434, 0x5fc2a340,
        0x72161dc3, 0x0cbce225, 0x8b283c49, 0x41ff0d95,
        0x7139a801, 0xde080cb3, 0x9cd8b4e4, 0x906456c1,
        0x617bcb84, 0x70d532b6, 0x74486c5c, 0x42d0b857
    };
    private static int[/*256*/] Td2 =
    {
        0xa75051f4, 0x65537e41, 0xa4c31a17, 0x5e963a27,
        0x6bcb3bab, 0x45f11f9d, 0x58abacfa, 0x03934be3,
        0xfa552030, 0x6df6ad76, 0x769188cc, 0x4c25f502,
        0xd7fc4fe5, 0xcbd7c52a, 0x44802635, 0xa38fb562,
        0x5a49deb1, 0x1b6725ba, 0x0e9845ea, 0xc0e15dfe,
        0x7502c32f, 0xf012814c, 0x97a38d46, 0xf9c66bd3,
        0x5fe7038f, 0x9c951592, 0x7aebbf6d, 0x59da9552,
        0x832dd4be, 0x21d35874, 0x692949e0, 0xc8448ec9,
        0x896a75c2, 0x7978f48e, 0x3e6b9958, 0x71dd27b9,
        0x4fb6bee1, 0xad17f088, 0xac66c920, 0x3ab47dce,
        0x4a1863df, 0x3182e51a, 0x33609751, 0x7f456253,
        0x77e0b164, 0xae84bb6b, 0xa01cfe81, 0x2b94f908,
        0x68587048, 0xfd198f45, 0x6c8794de, 0xf8b7527b,
        0xd323ab73, 0x02e2724b, 0x8f57e31f, 0xab2a6655,
        0x2807b2eb, 0xc2032fb5, 0x7b9a86c5, 0x08a5d337,
        0x87f23028, 0xa5b223bf, 0x6aba0203, 0x825ced16,
        0x1c2b8acf, 0xb492a779, 0xf2f0f307, 0xe2a14e69,
        0xf4cd65da, 0xbed50605, 0x621fd134, 0xfe8ac4a6,
        0x539d342e, 0x55a0a2f3, 0xe132058a, 0xeb75a4f6,
        0xec390b83, 0xefaa4060, 0x9f065e71, 0x1051bd6e,
        0x8af93e21, 0x063d96dd, 0x05aedd3e, 0xbd464de6,
        0x8db59154, 0x5d0571c4, 0xd46f0406, 0x15ff6050,
        0xfb241998, 0xe997d6bd, 0x43cc8940, 0x9e7767d9,
        0x42bdb0e8, 0x8b880789, 0x5b38e719, 0xeedb79c8,
        0x0a47a17c, 0x0fe97c42, 0x1ec9f884, 0x00000000,
        0x86830980, 0xed48322b, 0x70ac1e11, 0x724e6c5a,
        0xfffbfd0e, 0x38560f85, 0xd51e3dae, 0x3927362d,
        0xd9640a0f, 0xa621685c, 0x54d19b5b, 0x2e3a2436,
        0x67b10c0a, 0xe70f9357, 0x96d2b4ee, 0x919e1b9b,
        0xc54f80c0, 0x20a261dc, 0x4b695a77, 0x1a161c12,
        0xba0ae293, 0x2ae5c0a0, 0xe0433c22, 0x171d121b,
        0x0d0b0e09, 0xc7adf28b, 0xa8b92db6, 0xa9c8141e,
        0x198557f1, 0x074caf75, 0xddbbee99, 0x60fda37f,
        0x269ff701, 0xf5bc5c72, 0x3bc54466, 0x7e345bfb,
        0x29768b43, 0xc6dccb23, 0xfc68b6ed, 0xf163b8e4,
        0xdccad731, 0x85104263, 0x22401397, 0x112084c6,
        0x247d854a, 0x3df8d2bb, 0x3211aef9, 0xa16dc729,
        0x2f4b1d9e, 0x30f3dcb2, 0x52ec0d86, 0xe3d077c1,
        0x166c2bb3, 0xb999a970, 0x48fa1194, 0x642247e9,
        0x8cc4a8fc, 0x3f1aa0f0, 0x2cd8567d, 0x90ef2233,
        0x4ec78749, 0xd1c1d938, 0xa2fe8cca, 0x0b3698d4,
        0x81cfa6f5, 0xde28a57a, 0x8e26dab7, 0xbfa43fad,
        0x9de42c3a, 0x920d5078, 0xcc9b6a5f, 0x4662547e,
        0x13c2f68d, 0xb8e890d8, 0xf75e2e39, 0xaff582c3,
        0x80be9f5d, 0x937c69d0, 0x2da96fd5, 0x12b3cf25,
        0x993bc8ac, 0x7da71018, 0x636ee89c, 0xbb7bdb3b,
        0x7809cd26, 0x18f46e59, 0xb701ec9a, 0x9aa8834f,
        0x6e65e695, 0xe67eaaff, 0xcf0821bc, 0xe8e6ef15,
        0x9bd9bae7, 0x36ce4a6f, 0x09d4ea9f, 0x7cd629b0,
        0xb2af31a4, 0x23312a3f, 0x9430c6a5, 0x66c035a2,
        0xbc37744e, 0xcaa6fc82, 0xd0b0e090, 0xd81533a7,
        0x984af104, 0xdaf741ec, 0x500e7fcd, 0xf62f1791,
        0xd68d764d, 0xb04d43ef, 0x4d54ccaa, 0x04dfe496,
        0xb5e39ed1, 0x881b4c6a, 0x1fb8c12c, 0x517f4665,
        0xea049d5e, 0x355d018c, 0x7473fa87, 0x412efb0b,
        0x1d5ab367, 0xd25292db, 0x5633e910, 0x47136dd6,
        0x618c9ad7, 0x0c7a37a1, 0x148e59f8, 0x3c89eb13,
        0x27eecea9, 0xc935b761, 0xe5ede11c, 0xb13c7a47,
        0xdf599cd2, 0x733f55f2, 0xce791814, 0x37bf73c7,
        0xcdea53f7, 0xaa5b5ffd, 0x6f14df3d, 0xdb867844,
        0xf381caaf, 0xc43eb968, 0x342c3824, 0x405fc2a3,
        0xc372161d, 0x250cbce2, 0x498b283c, 0x9541ff0d,
        0x017139a8, 0xb3de080c, 0xe49cd8b4, 0xc1906456,
        0x84617bcb, 0xb670d532, 0x5c74486c, 0x5742d0b8
    };
    private static int[/*256*/] Td3 =
    {
        0xf4a75051, 0x4165537e, 0x17a4c31a, 0x275e963a,
        0xab6bcb3b, 0x9d45f11f, 0xfa58abac, 0xe303934b,
        0x30fa5520, 0x766df6ad, 0xcc769188, 0x024c25f5,
        0xe5d7fc4f, 0x2acbd7c5, 0x35448026, 0x62a38fb5,
        0xb15a49de, 0xba1b6725, 0xea0e9845, 0xfec0e15d,
        0x2f7502c3, 0x4cf01281, 0x4697a38d, 0xd3f9c66b,
        0x8f5fe703, 0x929c9515, 0x6d7aebbf, 0x5259da95,
        0xbe832dd4, 0x7421d358, 0xe0692949, 0xc9c8448e,
        0xc2896a75, 0x8e7978f4, 0x583e6b99, 0xb971dd27,
        0xe14fb6be, 0x88ad17f0, 0x20ac66c9, 0xce3ab47d,
        0xdf4a1863, 0x1a3182e5, 0x51336097, 0x537f4562,
        0x6477e0b1, 0x6bae84bb, 0x81a01cfe, 0x082b94f9,
        0x48685870, 0x45fd198f, 0xde6c8794, 0x7bf8b752,
        0x73d323ab, 0x4b02e272, 0x1f8f57e3, 0x55ab2a66,
        0xeb2807b2, 0xb5c2032f, 0xc57b9a86, 0x3708a5d3,
        0x2887f230, 0xbfa5b223, 0x036aba02, 0x16825ced,
        0xcf1c2b8a, 0x79b492a7, 0x07f2f0f3, 0x69e2a14e,
        0xdaf4cd65, 0x05bed506, 0x34621fd1, 0xa6fe8ac4,
        0x2e539d34, 0xf355a0a2, 0x8ae13205, 0xf6eb75a4,
        0x83ec390b, 0x60efaa40, 0x719f065e, 0x6e1051bd,
        0x218af93e, 0xdd063d96, 0x3e05aedd, 0xe6bd464d,
        0x548db591, 0xc45d0571, 0x06d46f04, 0x5015ff60,
        0x98fb2419, 0xbde997d6, 0x4043cc89, 0xd99e7767,
        0xe842bdb0, 0x898b8807, 0x195b38e7, 0xc8eedb79,
        0x7c0a47a1, 0x420fe97c, 0x841ec9f8, 0x00000000,
        0x80868309, 0x2bed4832, 0x1170ac1e, 0x5a724e6c,
        0x0efffbfd, 0x8538560f, 0xaed51e3d, 0x2d392736,
        0x0fd9640a, 0x5ca62168, 0x5b54d19b, 0x362e3a24,
        0x0a67b10c, 0x57e70f93, 0xee96d2b4, 0x9b919e1b,
        0xc0c54f80, 0xdc20a261, 0x774b695a, 0x121a161c,
        0x93ba0ae2, 0xa02ae5c0, 0x22e0433c, 0x1b171d12,
        0x090d0b0e, 0x8bc7adf2, 0xb6a8b92d, 0x1ea9c814,
        0xf1198557, 0x75074caf, 0x99ddbbee, 0x7f60fda3,
        0x01269ff7, 0x72f5bc5c, 0x663bc544, 0xfb7e345b,
        0x4329768b, 0x23c6dccb, 0xedfc68b6, 0xe4f163b8,
        0x31dccad7, 0x63851042, 0x97224013, 0xc6112084,
        0x4a247d85, 0xbb3df8d2, 0xf93211ae, 0x29a16dc7,
        0x9e2f4b1d, 0xb230f3dc, 0x8652ec0d, 0xc1e3d077,
        0xb3166c2b, 0x70b999a9, 0x9448fa11, 0xe9642247,
        0xfc8cc4a8, 0xf03f1aa0, 0x7d2cd856, 0x3390ef22,
        0x494ec787, 0x38d1c1d9, 0xcaa2fe8c, 0xd40b3698,
        0xf581cfa6, 0x7ade28a5, 0xb78e26da, 0xadbfa43f,
        0x3a9de42c, 0x78920d50, 0x5fcc9b6a, 0x7e466254,
        0x8d13c2f6, 0xd8b8e890, 0x39f75e2e, 0xc3aff582,
        0x5d80be9f, 0xd0937c69, 0xd52da96f, 0x2512b3cf,
        0xac993bc8, 0x187da710, 0x9c636ee8, 0x3bbb7bdb,
        0x267809cd, 0x5918f46e, 0x9ab701ec, 0x4f9aa883,
        0x956e65e6, 0xffe67eaa, 0xbccf0821, 0x15e8e6ef,
        0xe79bd9ba, 0x6f36ce4a, 0x9f09d4ea, 0xb07cd629,
        0xa4b2af31, 0x3f23312a, 0xa59430c6, 0xa266c035,
        0x4ebc3774, 0x82caa6fc, 0x90d0b0e0, 0xa7d81533,
        0x04984af1, 0xecdaf741, 0xcd500e7f, 0x91f62f17,
        0x4dd68d76, 0xefb04d43, 0xaa4d54cc, 0x9604dfe4,
        0xd1b5e39e, 0x6a881b4c, 0x2c1fb8c1, 0x65517f46,
        0x5eea049d, 0x8c355d01, 0x877473fa, 0x0b412efb,
        0x671d5ab3, 0xdbd25292, 0x105633e9, 0xd647136d,
        0xd7618c9a, 0xa10c7a37, 0xf8148e59, 0x133c89eb,
        0xa927eece, 0x61c935b7, 0x1ce5ede1, 0x47b13c7a,
        0xd2df599c, 0xf2733f55, 0x14ce7918, 0xc737bf73,
        0xf7cdea53, 0xfdaa5b5f, 0x3d6f14df, 0x44db8678,
        0xaff381ca, 0x68c43eb9, 0x24342c38, 0xa3405fc2,
        0x1dc37216, 0xe2250cbc, 0x3c498b28, 0x0d9541ff,
        0xa8017139, 0x0cb3de08, 0xb4e49cd8, 0x56c19064,
        0xcb84617b, 0x32b670d5, 0x6c5c7448, 0xb85742d0
    };
    private static int[/*256*/] Td4 =
    {
        0x52525252, 0x09090909, 0x6a6a6a6a, 0xd5d5d5d5,
        0x30303030, 0x36363636, 0xa5a5a5a5, 0x38383838,
        0xbfbfbfbf, 0x40404040, 0xa3a3a3a3, 0x9e9e9e9e,
        0x81818181, 0xf3f3f3f3, 0xd7d7d7d7, 0xfbfbfbfb,
        0x7c7c7c7c, 0xe3e3e3e3, 0x39393939, 0x82828282,
        0x9b9b9b9b, 0x2f2f2f2f, 0xffffffff, 0x87878787,
        0x34343434, 0x8e8e8e8e, 0x43434343, 0x44444444,
        0xc4c4c4c4, 0xdededede, 0xe9e9e9e9, 0xcbcbcbcb,
        0x54545454, 0x7b7b7b7b, 0x94949494, 0x32323232,
        0xa6a6a6a6, 0xc2c2c2c2, 0x23232323, 0x3d3d3d3d,
        0xeeeeeeee, 0x4c4c4c4c, 0x95959595, 0x0b0b0b0b,
        0x42424242, 0xfafafafa, 0xc3c3c3c3, 0x4e4e4e4e,
        0x08080808, 0x2e2e2e2e, 0xa1a1a1a1, 0x66666666,
        0x28282828, 0xd9d9d9d9, 0x24242424, 0xb2b2b2b2,
        0x76767676, 0x5b5b5b5b, 0xa2a2a2a2, 0x49494949,
        0x6d6d6d6d, 0x8b8b8b8b, 0xd1d1d1d1, 0x25252525,
        0x72727272, 0xf8f8f8f8, 0xf6f6f6f6, 0x64646464,
        0x86868686, 0x68686868, 0x98989898, 0x16161616,
        0xd4d4d4d4, 0xa4a4a4a4, 0x5c5c5c5c, 0xcccccccc,
        0x5d5d5d5d, 0x65656565, 0xb6b6b6b6, 0x92929292,
        0x6c6c6c6c, 0x70707070, 0x48484848, 0x50505050,
        0xfdfdfdfd, 0xedededed, 0xb9b9b9b9, 0xdadadada,
        0x5e5e5e5e, 0x15151515, 0x46464646, 0x57575757,
        0xa7a7a7a7, 0x8d8d8d8d, 0x9d9d9d9d, 0x84848484,
        0x90909090, 0xd8d8d8d8, 0xabababab, 0x00000000,
        0x8c8c8c8c, 0xbcbcbcbc, 0xd3d3d3d3, 0x0a0a0a0a,
        0xf7f7f7f7, 0xe4e4e4e4, 0x58585858, 0x05050505,
        0xb8b8b8b8, 0xb3b3b3b3, 0x45454545, 0x06060606,
        0xd0d0d0d0, 0x2c2c2c2c, 0x1e1e1e1e, 0x8f8f8f8f,
        0xcacacaca, 0x3f3f3f3f, 0x0f0f0f0f, 0x02020202,
        0xc1c1c1c1, 0xafafafaf, 0xbdbdbdbd, 0x03030303,
        0x01010101, 0x13131313, 0x8a8a8a8a, 0x6b6b6b6b,
        0x3a3a3a3a, 0x91919191, 0x11111111, 0x41414141,
        0x4f4f4f4f, 0x67676767, 0xdcdcdcdc, 0xeaeaeaea,
        0x97979797, 0xf2f2f2f2, 0xcfcfcfcf, 0xcececece,
        0xf0f0f0f0, 0xb4b4b4b4, 0xe6e6e6e6, 0x73737373,
        0x96969696, 0xacacacac, 0x74747474, 0x22222222,
        0xe7e7e7e7, 0xadadadad, 0x35353535, 0x85858585,
        0xe2e2e2e2, 0xf9f9f9f9, 0x37373737, 0xe8e8e8e8,
        0x1c1c1c1c, 0x75757575, 0xdfdfdfdf, 0x6e6e6e6e,
        0x47474747, 0xf1f1f1f1, 0x1a1a1a1a, 0x71717171,
        0x1d1d1d1d, 0x29292929, 0xc5c5c5c5, 0x89898989,
        0x6f6f6f6f, 0xb7b7b7b7, 0x62626262, 0x0e0e0e0e,
        0xaaaaaaaa, 0x18181818, 0xbebebebe, 0x1b1b1b1b,
        0xfcfcfcfc, 0x56565656, 0x3e3e3e3e, 0x4b4b4b4b,
        0xc6c6c6c6, 0xd2d2d2d2, 0x79797979, 0x20202020,
        0x9a9a9a9a, 0xdbdbdbdb, 0xc0c0c0c0, 0xfefefefe,
        0x78787878, 0xcdcdcdcd, 0x5a5a5a5a, 0xf4f4f4f4,
        0x1f1f1f1f, 0xdddddddd, 0xa8a8a8a8, 0x33333333,
        0x88888888, 0x07070707, 0xc7c7c7c7, 0x31313131,
        0xb1b1b1b1, 0x12121212, 0x10101010, 0x59595959,
        0x27272727, 0x80808080, 0xecececec, 0x5f5f5f5f,
        0x60606060, 0x51515151, 0x7f7f7f7f, 0xa9a9a9a9,
        0x19191919, 0xb5b5b5b5, 0x4a4a4a4a, 0x0d0d0d0d,
        0x2d2d2d2d, 0xe5e5e5e5, 0x7a7a7a7a, 0x9f9f9f9f,
        0x93939393, 0xc9c9c9c9, 0x9c9c9c9c, 0xefefefef,
        0xa0a0a0a0, 0xe0e0e0e0, 0x3b3b3b3b, 0x4d4d4d4d,
        0xaeaeaeae, 0x2a2a2a2a, 0xf5f5f5f5, 0xb0b0b0b0,
        0xc8c8c8c8, 0xebebebeb, 0xbbbbbbbb, 0x3c3c3c3c,
        0x83838383, 0x53535353, 0x99999999, 0x61616161,
        0x17171717, 0x2b2b2b2b, 0x04040404, 0x7e7e7e7e,
        0xbabababa, 0x77777777, 0xd6d6d6d6, 0x26262626,
        0xe1e1e1e1, 0x69696969, 0x14141414, 0x63636363,
        0x55555555, 0x21212121, 0x0c0c0c0c, 0x7d7d7d7d
    };

    /*
        Table for use in "RijndaelEncKeyExpansionV20":
        (For 128-bit blocks, Rijndael never uses more than 10 rcon values.)
    */
    private static int[/*10*/] rcon =
    {
        0x01000000, 0x02000000, 0x04000000, 0x08000000,
        0x10000000, 0x20000000, 0x40000000, 0x80000000,
        0x1B000000, 0x36000000
    };

    /* 
        Internal module for expansion of key into encryption key schedule
        @ Input : anSessionKey : initial key
                  anSchedule : buffer for expanded key schedule
        @ Output: anSchedule : holds expanded key schedule
        @ Return: none
    */
    public static void RijndaelEncKeyExpansionV20
    (int[/*4*/] anSessionKey, int[/*4*11*/] anSchedule)
    {
        int i = 0;
        int temp;

        anSchedule[0] = anSessionKey[0];
        anSchedule[1] = anSessionKey[1];
        anSchedule[2] = anSessionKey[2];
        anSchedule[3] = anSessionKey[3];
        for (;;) {
            temp = anSchedule[4 * i + 3];
            anSchedule[4 * i + 4] = anSchedule[4 * i] ^
                                    (Te4[(temp >>> 16) & 0x000000ff] & 0xff000000) ^
                                    (Te4[(temp >>>  8) & 0x000000ff] & 0x00ff0000) ^
                                    (Te4[(temp      ) & 0x000000ff] & 0x0000ff00) ^
                                    (Te4[(temp >>> 24)             ] & 0x000000ff) ^
                                    rcon[i];
            anSchedule[4 * i + 5] = anSchedule[4 * i + 1] ^ anSchedule[4 * i + 4];
            anSchedule[4 * i + 6] = anSchedule[4 * i + 2] ^ anSchedule[4 * i + 5];
            anSchedule[4 * i + 7] = anSchedule[4 * i + 3] ^ anSchedule[4 * i + 6];
            if (++i == 10)
                return;
        }
    }

    /* 
        Internal module for expansion of key into decryption key schedule
        @ Input : aunKey : initial key
                  aunSchedule : buffer for expanded key schedule
        @ Output: aunSchedule : holds expanded key schedule
        @ Return: none
    */
    public static void RijndaelDecKeyExpansionV20
    (int[/*4*/] anSessionKey, int[/*4*11*/] anSchedule)
    {
        int i, j;
        int temp;

        /* Expand the key into the encryption key schedule: */
        RijndaelEncKeyExpansionV20(anSessionKey, anSchedule);
        /* Invert the order of the round keys: */
        for (i = 0, j = 4 * 10; i < j; i += 4, j -= 4) {
            temp = anSchedule[i    ]; anSchedule[i    ] = anSchedule[j    ]; anSchedule[j    ] = temp;
            temp = anSchedule[i + 1]; anSchedule[i + 1] = anSchedule[j + 1]; anSchedule[j + 1] = temp;
            temp = anSchedule[i + 2]; anSchedule[i + 2] = anSchedule[j + 2]; anSchedule[j + 2] = temp;
            temp = anSchedule[i + 3]; anSchedule[i + 3] = anSchedule[j + 3]; anSchedule[j + 3] = temp;
        }
        /* Apply the inverse MixColumns transformation to all round keys but the first and the last: */
        for (i = 1; i < 10; i++) {
            anSchedule[4 * i    ] = Td0[Te4[(anSchedule[4 * i    ] >>> 24)             ] & 0x000000ff] ^
                                    Td1[Te4[(anSchedule[4 * i    ] >>> 16) & 0x000000ff] & 0x000000ff] ^
                                    Td2[Te4[(anSchedule[4 * i    ] >>>  8) & 0x000000ff] & 0x000000ff] ^
                                    Td3[Te4[(anSchedule[4 * i    ]       ) & 0x000000ff] & 0x000000ff];
            anSchedule[4 * i + 1] = Td0[Te4[(anSchedule[4 * i + 1] >>> 24)             ] & 0x000000ff] ^
                                    Td1[Te4[(anSchedule[4 * i + 1] >>> 16) & 0x000000ff] & 0x000000ff] ^
                                    Td2[Te4[(anSchedule[4 * i + 1] >>>  8) & 0x000000ff] & 0x000000ff] ^
                                    Td3[Te4[(anSchedule[4 * i + 1]       ) & 0x000000ff] & 0x000000ff];
            anSchedule[4 * i + 2] = Td0[Te4[(anSchedule[4 * i + 2] >>> 24)             ] & 0x000000ff] ^
                                    Td1[Te4[(anSchedule[4 * i + 2] >>> 16) & 0x000000ff] & 0x000000ff] ^
                                    Td2[Te4[(anSchedule[4 * i + 2] >>>  8) & 0x000000ff] & 0x000000ff] ^
                                    Td3[Te4[(anSchedule[4 * i + 2]       ) & 0x000000ff] & 0x000000ff];
            anSchedule[4 * i + 3] = Td0[Te4[(anSchedule[4 * i + 3] >>> 24)             ] & 0x000000ff] ^
                                    Td1[Te4[(anSchedule[4 * i + 3] >>> 16) & 0x000000ff] & 0x000000ff] ^
                                    Td2[Te4[(anSchedule[4 * i + 3] >>>  8) & 0x000000ff] & 0x000000ff] ^
                                    Td3[Te4[(anSchedule[4 * i + 3]       ) & 0x000000ff] & 0x000000ff];
        }
    }

    private static int GETN(byte[/*4*/] b, int nStartIndex)
    {
        return ( (int)(b[nStartIndex    ]) << 24              ) ^
               (((int)(b[nStartIndex + 1]) << 16) & 0x00ff0000) ^
               (((int)(b[nStartIndex + 2]) <<  8) & 0x0000ff00) ^
               ( (int)(b[nStartIndex + 3])        & 0x000000ff);
    }

    private static void PUTN(byte[/*4*/] ab, int startindex, int n)
    {
        ab[startindex    ] = (byte)((n >>> 24) & 0x000000ff);
        ab[startindex + 1] = (byte)((n >>> 16) & 0x000000ff);
        ab[startindex + 2] = (byte)((n >>>  8) & 0x000000ff);
        ab[startindex + 3] = (byte)( n         & 0x000000ff);
    }

    /*
        Internal module for block encryption in CBC-mode operation
        @ Input : abPlain : array of plain text bytes
                  nPlainStartIndex : index in "abPlain" from which to start encryption
                  nPlainBytes : # of bytes to be encrypted; arbitrary positive length
                  anSchedule : expanded encryption key schedule
                  anInitVector : initialization vector for CBC-mode operation
                  abCipher : buffer for cipher text bytes; assumed to have sufficient length
                  nCipherStartIndex : index in "abCipher" from which to write encrypted text bytes
        @ Output: anInitVector : contains the last cipher block
                  abCipher : contains all the cipher blocks (as bytes)
        @ Return: # of 16-byte blocks that have been encrypted
    */
    private static int RijndaelCBCEncryptV20
    (byte[] abPlain,
     int nPlainStartIndex,
     int nPlainBytes,
     int[/*4*11*/] anSchedule,
     int[/*4*/] anInitVector,
     byte[] abCipher,
     int nCipherStartIndex)
    {
        int nBlockNum, i, j;
        int[] s = new int[4];
        int[] t = new int[4];

        /* # of 16-byte blocks to be encrypted: */
        nBlockNum = (nPlainBytes % 16 == 0) ? (nPlainBytes / 16) : (nPlainBytes / 16 + 1);

        for (i = 0; i < nBlockNum; ++i) {
            for (j = 0; j < 4; ++j)
                s[j] = anInitVector[j];

            /* Previous cipher block ^ current plain block ^ initial round key: */
            if ((nPlainBytes % 16 != 0) && (i == nBlockNum - 1)) {
                for (j = 16 * i; j < nPlainBytes; ++j)
                    abCipher[nCipherStartIndex + j] = abPlain[nPlainStartIndex + j];
            } else {
                for (j = 16 * i; j < 16 * (i + 1); ++j)
                    abCipher[nCipherStartIndex + j] = abPlain[nPlainStartIndex + j];
            }
            s[0] ^= GETN(abCipher, nCipherStartIndex + 16 * i     ) ^ anSchedule[0];
            s[1] ^= GETN(abCipher, nCipherStartIndex + 16 * i +  4) ^ anSchedule[1];
            s[2] ^= GETN(abCipher, nCipherStartIndex + 16 * i +  8) ^ anSchedule[2];
            s[3] ^= GETN(abCipher, nCipherStartIndex + 16 * i + 12) ^ anSchedule[3];

            /* Round 1: */
            t[0] = Te0[s[0] >>> 24] ^ Te1[(s[1] >>> 16) & 0x000000ff] ^ Te2[(s[2] >>>  8) & 0x000000ff] ^ Te3[s[3] & 0x000000ff] ^ anSchedule[ 4];
            t[1] = Te0[s[1] >>> 24] ^ Te1[(s[2] >>> 16) & 0x000000ff] ^ Te2[(s[3] >>>  8) & 0x000000ff] ^ Te3[s[0] & 0x000000ff] ^ anSchedule[ 5];
            t[2] = Te0[s[2] >>> 24] ^ Te1[(s[3] >>> 16) & 0x000000ff] ^ Te2[(s[0] >>>  8) & 0x000000ff] ^ Te3[s[1] & 0x000000ff] ^ anSchedule[ 6];
            t[3] = Te0[s[3] >>> 24] ^ Te1[(s[0] >>> 16) & 0x000000ff] ^ Te2[(s[1] >>>  8) & 0x000000ff] ^ Te3[s[2] & 0x000000ff] ^ anSchedule[ 7];
            /* Round 2: */
            s[0] = Te0[t[0] >>> 24] ^ Te1[(t[1] >>> 16) & 0x000000ff] ^ Te2[(t[2] >>>  8) & 0x000000ff] ^ Te3[t[3] & 0x000000ff] ^ anSchedule[ 8];
            s[1] = Te0[t[1] >>> 24] ^ Te1[(t[2] >>> 16) & 0x000000ff] ^ Te2[(t[3] >>>  8) & 0x000000ff] ^ Te3[t[0] & 0x000000ff] ^ anSchedule[ 9];
            s[2] = Te0[t[2] >>> 24] ^ Te1[(t[3] >>> 16) & 0x000000ff] ^ Te2[(t[0] >>>  8) & 0x000000ff] ^ Te3[t[1] & 0x000000ff] ^ anSchedule[10];
            s[3] = Te0[t[3] >>> 24] ^ Te1[(t[0] >>> 16) & 0x000000ff] ^ Te2[(t[1] >>>  8) & 0x000000ff] ^ Te3[t[2] & 0x000000ff] ^ anSchedule[11];
            /* Round 3: */
            t[0] = Te0[s[0] >>> 24] ^ Te1[(s[1] >>> 16) & 0x000000ff] ^ Te2[(s[2] >>>  8) & 0x000000ff] ^ Te3[s[3] & 0x000000ff] ^ anSchedule[12];
            t[1] = Te0[s[1] >>> 24] ^ Te1[(s[2] >>> 16) & 0x000000ff] ^ Te2[(s[3] >>>  8) & 0x000000ff] ^ Te3[s[0] & 0x000000ff] ^ anSchedule[13];
            t[2] = Te0[s[2] >>> 24] ^ Te1[(s[3] >>> 16) & 0x000000ff] ^ Te2[(s[0] >>>  8) & 0x000000ff] ^ Te3[s[1] & 0x000000ff] ^ anSchedule[14];
            t[3] = Te0[s[3] >>> 24] ^ Te1[(s[0] >>> 16) & 0x000000ff] ^ Te2[(s[1] >>>  8) & 0x000000ff] ^ Te3[s[2] & 0x000000ff] ^ anSchedule[15];
            /* Round 4: */
            s[0] = Te0[t[0] >>> 24] ^ Te1[(t[1] >>> 16) & 0x000000ff] ^ Te2[(t[2] >>>  8) & 0x000000ff] ^ Te3[t[3] & 0x000000ff] ^ anSchedule[16];
            s[1] = Te0[t[1] >>> 24] ^ Te1[(t[2] >>> 16) & 0x000000ff] ^ Te2[(t[3] >>>  8) & 0x000000ff] ^ Te3[t[0] & 0x000000ff] ^ anSchedule[17];
            s[2] = Te0[t[2] >>> 24] ^ Te1[(t[3] >>> 16) & 0x000000ff] ^ Te2[(t[0] >>>  8) & 0x000000ff] ^ Te3[t[1] & 0x000000ff] ^ anSchedule[18];
            s[3] = Te0[t[3] >>> 24] ^ Te1[(t[0] >>> 16) & 0x000000ff] ^ Te2[(t[1] >>>  8) & 0x000000ff] ^ Te3[t[2] & 0x000000ff] ^ anSchedule[19];
            /* Round 5: */
            t[0] = Te0[s[0] >>> 24] ^ Te1[(s[1] >>> 16) & 0x000000ff] ^ Te2[(s[2] >>>  8) & 0x000000ff] ^ Te3[s[3] & 0x000000ff] ^ anSchedule[20];
            t[1] = Te0[s[1] >>> 24] ^ Te1[(s[2] >>> 16) & 0x000000ff] ^ Te2[(s[3] >>>  8) & 0x000000ff] ^ Te3[s[0] & 0x000000ff] ^ anSchedule[21];
            t[2] = Te0[s[2] >>> 24] ^ Te1[(s[3] >>> 16) & 0x000000ff] ^ Te2[(s[0] >>>  8) & 0x000000ff] ^ Te3[s[1] & 0x000000ff] ^ anSchedule[22];
            t[3] = Te0[s[3] >>> 24] ^ Te1[(s[0] >>> 16) & 0x000000ff] ^ Te2[(s[1] >>>  8) & 0x000000ff] ^ Te3[s[2] & 0x000000ff] ^ anSchedule[23];
            /* Round 6: */
            s[0] = Te0[t[0] >>> 24] ^ Te1[(t[1] >>> 16) & 0x000000ff] ^ Te2[(t[2] >>>  8) & 0x000000ff] ^ Te3[t[3] & 0x000000ff] ^ anSchedule[24];
            s[1] = Te0[t[1] >>> 24] ^ Te1[(t[2] >>> 16) & 0x000000ff] ^ Te2[(t[3] >>>  8) & 0x000000ff] ^ Te3[t[0] & 0x000000ff] ^ anSchedule[25];
            s[2] = Te0[t[2] >>> 24] ^ Te1[(t[3] >>> 16) & 0x000000ff] ^ Te2[(t[0] >>>  8) & 0x000000ff] ^ Te3[t[1] & 0x000000ff] ^ anSchedule[26];
            s[3] = Te0[t[3] >>> 24] ^ Te1[(t[0] >>> 16) & 0x000000ff] ^ Te2[(t[1] >>>  8) & 0x000000ff] ^ Te3[t[2] & 0x000000ff] ^ anSchedule[27];
            /* Round 7: */
            t[0] = Te0[s[0] >>> 24] ^ Te1[(s[1] >>> 16) & 0x000000ff] ^ Te2[(s[2] >>>  8) & 0x000000ff] ^ Te3[s[3] & 0x000000ff] ^ anSchedule[28];
            t[1] = Te0[s[1] >>> 24] ^ Te1[(s[2] >>> 16) & 0x000000ff] ^ Te2[(s[3] >>>  8) & 0x000000ff] ^ Te3[s[0] & 0x000000ff] ^ anSchedule[29];
            t[2] = Te0[s[2] >>> 24] ^ Te1[(s[3] >>> 16) & 0x000000ff] ^ Te2[(s[0] >>>  8) & 0x000000ff] ^ Te3[s[1] & 0x000000ff] ^ anSchedule[30];
            t[3] = Te0[s[3] >>> 24] ^ Te1[(s[0] >>> 16) & 0x000000ff] ^ Te2[(s[1] >>>  8) & 0x000000ff] ^ Te3[s[2] & 0x000000ff] ^ anSchedule[31];
            /* Round 8: */
            s[0] = Te0[t[0] >>> 24] ^ Te1[(t[1] >>> 16) & 0x000000ff] ^ Te2[(t[2] >>>  8) & 0x000000ff] ^ Te3[t[3] & 0x000000ff] ^ anSchedule[32];
            s[1] = Te0[t[1] >>> 24] ^ Te1[(t[2] >>> 16) & 0x000000ff] ^ Te2[(t[3] >>>  8) & 0x000000ff] ^ Te3[t[0] & 0x000000ff] ^ anSchedule[33];
            s[2] = Te0[t[2] >>> 24] ^ Te1[(t[3] >>> 16) & 0x000000ff] ^ Te2[(t[0] >>>  8) & 0x000000ff] ^ Te3[t[1] & 0x000000ff] ^ anSchedule[34];
            s[3] = Te0[t[3] >>> 24] ^ Te1[(t[0] >>> 16) & 0x000000ff] ^ Te2[(t[1] >>>  8) & 0x000000ff] ^ Te3[t[2] & 0x000000ff] ^ anSchedule[35];
            /* Round 9: */
            t[0] = Te0[s[0] >>> 24] ^ Te1[(s[1] >>> 16) & 0x000000ff] ^ Te2[(s[2] >>>  8) & 0x000000ff] ^ Te3[s[3] & 0x000000ff] ^ anSchedule[36];
            t[1] = Te0[s[1] >>> 24] ^ Te1[(s[2] >>> 16) & 0x000000ff] ^ Te2[(s[3] >>>  8) & 0x000000ff] ^ Te3[s[0] & 0x000000ff] ^ anSchedule[37];
            t[2] = Te0[s[2] >>> 24] ^ Te1[(s[3] >>> 16) & 0x000000ff] ^ Te2[(s[0] >>>  8) & 0x000000ff] ^ Te3[s[1] & 0x000000ff] ^ anSchedule[38];
            t[3] = Te0[s[3] >>> 24] ^ Te1[(s[0] >>> 16) & 0x000000ff] ^ Te2[(s[1] >>>  8) & 0x000000ff] ^ Te3[s[2] & 0x000000ff] ^ anSchedule[39];
            /* Round 10 is special: */
            s[0] = (Te4[(t[0] >>> 24)             ] & 0xff000000) ^
                   (Te4[(t[1] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Te4[(t[2] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Te4[(t[3]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[40];
            s[1] = (Te4[(t[1] >>> 24)             ] & 0xff000000) ^
                   (Te4[(t[2] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Te4[(t[3] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Te4[(t[0]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[41];
            s[2] = (Te4[(t[2] >>> 24)             ] & 0xff000000) ^
                   (Te4[(t[3] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Te4[(t[0] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Te4[(t[1]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[42];
            s[3] = (Te4[(t[3] >>> 24)             ] & 0xff000000) ^
                   (Te4[(t[0] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Te4[(t[1] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Te4[(t[2]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[43];

            /* Map cipher state to initialization vector and output block: */
            for (j = 0; j < 4; ++j)
                anInitVector[j] = s[j];
            PUTN(abCipher, nCipherStartIndex + 16 * i     , s[0]);
            PUTN(abCipher, nCipherStartIndex + 16 * i +  4, s[1]);
            PUTN(abCipher, nCipherStartIndex + 16 * i +  8, s[2]);
            PUTN(abCipher, nCipherStartIndex + 16 * i + 12, s[3]);
        }

        return nBlockNum;
    }

    /*
        Internal module for block decryption in CBC-mode operation
        @ Input : abCipher : array of cipher text bytes
                  nCipherStartIndex : index in "abCipher" from which to start decryption
                  nCipherBlocks : # of blocks to be decrypted; multiple of 16
                  anSchedule : expanded decryption key schedule
                  anInitVector : initialization vector for CBC-mode operation
                  abPlain : buffer for plain text bytes; assumed to have sufficient length
                  nPlainStartIndex : index in "abPlain" from which to write decrypted text bytes
        @ Output: anInitVector : contains the last cipher block
                  abCipher : contains all the plain blocks (as bytes)
        @ Return: none
    */
    private static void RijndaelCBCDecryptV20
    (byte[] abCipher,
     int nCipherStartIndex,
     int nCipherBlocks,
     int[/*4*11*/] anSchedule,
     int[/*4*/] anInitVector,
     byte[] abPlain,
     int nPlainStartIndex)
    {
        int i;
        int[] s = new int[4];
        int[] t = new int[4];

        for (i = 0; i < nCipherBlocks; ++i) {
            /* Current cipher block ^ initial round key: */
            s[0] = GETN(abCipher, nCipherStartIndex + 16 * i     ) ^ anSchedule[0];
            s[1] = GETN(abCipher, nCipherStartIndex + 16 * i +  4) ^ anSchedule[1];
            s[2] = GETN(abCipher, nCipherStartIndex + 16 * i +  8) ^ anSchedule[2];
            s[3] = GETN(abCipher, nCipherStartIndex + 16 * i + 12) ^ anSchedule[3];

            /* Round 1: */
            t[0] = Td0[s[0] >>> 24] ^ Td1[(s[3] >>> 16) & 0x000000ff] ^ Td2[(s[2] >>>  8) & 0x000000ff] ^ Td3[s[1] & 0x000000ff] ^ anSchedule[ 4];
            t[1] = Td0[s[1] >>> 24] ^ Td1[(s[0] >>> 16) & 0x000000ff] ^ Td2[(s[3] >>>  8) & 0x000000ff] ^ Td3[s[2] & 0x000000ff] ^ anSchedule[ 5];
            t[2] = Td0[s[2] >>> 24] ^ Td1[(s[1] >>> 16) & 0x000000ff] ^ Td2[(s[0] >>>  8) & 0x000000ff] ^ Td3[s[3] & 0x000000ff] ^ anSchedule[ 6];
            t[3] = Td0[s[3] >>> 24] ^ Td1[(s[2] >>> 16) & 0x000000ff] ^ Td2[(s[1] >>>  8) & 0x000000ff] ^ Td3[s[0] & 0x000000ff] ^ anSchedule[ 7];
            /* Round 2: */
            s[0] = Td0[t[0] >>> 24] ^ Td1[(t[3] >>> 16) & 0x000000ff] ^ Td2[(t[2] >>>  8) & 0x000000ff] ^ Td3[t[1] & 0x000000ff] ^ anSchedule[ 8];
            s[1] = Td0[t[1] >>> 24] ^ Td1[(t[0] >>> 16) & 0x000000ff] ^ Td2[(t[3] >>>  8) & 0x000000ff] ^ Td3[t[2] & 0x000000ff] ^ anSchedule[ 9];
            s[2] = Td0[t[2] >>> 24] ^ Td1[(t[1] >>> 16) & 0x000000ff] ^ Td2[(t[0] >>>  8) & 0x000000ff] ^ Td3[t[3] & 0x000000ff] ^ anSchedule[10];
            s[3] = Td0[t[3] >>> 24] ^ Td1[(t[2] >>> 16) & 0x000000ff] ^ Td2[(t[1] >>>  8) & 0x000000ff] ^ Td3[t[0] & 0x000000ff] ^ anSchedule[11];
            /* Round 3: */
            t[0] = Td0[s[0] >>> 24] ^ Td1[(s[3] >>> 16) & 0x000000ff] ^ Td2[(s[2] >>>  8) & 0x000000ff] ^ Td3[s[1] & 0x000000ff] ^ anSchedule[12];
            t[1] = Td0[s[1] >>> 24] ^ Td1[(s[0] >>> 16) & 0x000000ff] ^ Td2[(s[3] >>>  8) & 0x000000ff] ^ Td3[s[2] & 0x000000ff] ^ anSchedule[13];
            t[2] = Td0[s[2] >>> 24] ^ Td1[(s[1] >>> 16) & 0x000000ff] ^ Td2[(s[0] >>>  8) & 0x000000ff] ^ Td3[s[3] & 0x000000ff] ^ anSchedule[14];
            t[3] = Td0[s[3] >>> 24] ^ Td1[(s[2] >>> 16) & 0x000000ff] ^ Td2[(s[1] >>>  8) & 0x000000ff] ^ Td3[s[0] & 0x000000ff] ^ anSchedule[15];
            /* Round 4: */
            s[0] = Td0[t[0] >>> 24] ^ Td1[(t[3] >>> 16) & 0x000000ff] ^ Td2[(t[2] >>>  8) & 0x000000ff] ^ Td3[t[1] & 0x000000ff] ^ anSchedule[16];
            s[1] = Td0[t[1] >>> 24] ^ Td1[(t[0] >>> 16) & 0x000000ff] ^ Td2[(t[3] >>>  8) & 0x000000ff] ^ Td3[t[2] & 0x000000ff] ^ anSchedule[17];
            s[2] = Td0[t[2] >>> 24] ^ Td1[(t[1] >>> 16) & 0x000000ff] ^ Td2[(t[0] >>>  8) & 0x000000ff] ^ Td3[t[3] & 0x000000ff] ^ anSchedule[18];
            s[3] = Td0[t[3] >>> 24] ^ Td1[(t[2] >>> 16) & 0x000000ff] ^ Td2[(t[1] >>>  8) & 0x000000ff] ^ Td3[t[0] & 0x000000ff] ^ anSchedule[19];
            /* Round 5: */
            t[0] = Td0[s[0] >>> 24] ^ Td1[(s[3] >>> 16) & 0x000000ff] ^ Td2[(s[2] >>>  8) & 0x000000ff] ^ Td3[s[1] & 0x000000ff] ^ anSchedule[20];
            t[1] = Td0[s[1] >>> 24] ^ Td1[(s[0] >>> 16) & 0x000000ff] ^ Td2[(s[3] >>>  8) & 0x000000ff] ^ Td3[s[2] & 0x000000ff] ^ anSchedule[21];
            t[2] = Td0[s[2] >>> 24] ^ Td1[(s[1] >>> 16) & 0x000000ff] ^ Td2[(s[0] >>>  8) & 0x000000ff] ^ Td3[s[3] & 0x000000ff] ^ anSchedule[22];
            t[3] = Td0[s[3] >>> 24] ^ Td1[(s[2] >>> 16) & 0x000000ff] ^ Td2[(s[1] >>>  8) & 0x000000ff] ^ Td3[s[0] & 0x000000ff] ^ anSchedule[23];
            /* Round 6: */
            s[0] = Td0[t[0] >>> 24] ^ Td1[(t[3] >>> 16) & 0x000000ff] ^ Td2[(t[2] >>>  8) & 0x000000ff] ^ Td3[t[1] & 0x000000ff] ^ anSchedule[24];
            s[1] = Td0[t[1] >>> 24] ^ Td1[(t[0] >>> 16) & 0x000000ff] ^ Td2[(t[3] >>>  8) & 0x000000ff] ^ Td3[t[2] & 0x000000ff] ^ anSchedule[25];
            s[2] = Td0[t[2] >>> 24] ^ Td1[(t[1] >>> 16) & 0x000000ff] ^ Td2[(t[0] >>>  8) & 0x000000ff] ^ Td3[t[3] & 0x000000ff] ^ anSchedule[26];
            s[3] = Td0[t[3] >>> 24] ^ Td1[(t[2] >>> 16) & 0x000000ff] ^ Td2[(t[1] >>>  8) & 0x000000ff] ^ Td3[t[0] & 0x000000ff] ^ anSchedule[27];
            /* Round 7: */
            t[0] = Td0[s[0] >>> 24] ^ Td1[(s[3] >>> 16) & 0x000000ff] ^ Td2[(s[2] >>>  8) & 0x000000ff] ^ Td3[s[1] & 0x000000ff] ^ anSchedule[28];
            t[1] = Td0[s[1] >>> 24] ^ Td1[(s[0] >>> 16) & 0x000000ff] ^ Td2[(s[3] >>>  8) & 0x000000ff] ^ Td3[s[2] & 0x000000ff] ^ anSchedule[29];
            t[2] = Td0[s[2] >>> 24] ^ Td1[(s[1] >>> 16) & 0x000000ff] ^ Td2[(s[0] >>>  8) & 0x000000ff] ^ Td3[s[3] & 0x000000ff] ^ anSchedule[30];
            t[3] = Td0[s[3] >>> 24] ^ Td1[(s[2] >>> 16) & 0x000000ff] ^ Td2[(s[1] >>>  8) & 0x000000ff] ^ Td3[s[0] & 0x000000ff] ^ anSchedule[31];
            /* Round 8: */
            s[0] = Td0[t[0] >>> 24] ^ Td1[(t[3] >>> 16) & 0x000000ff] ^ Td2[(t[2] >>>  8) & 0x000000ff] ^ Td3[t[1] & 0x000000ff] ^ anSchedule[32];
            s[1] = Td0[t[1] >>> 24] ^ Td1[(t[0] >>> 16) & 0x000000ff] ^ Td2[(t[3] >>>  8) & 0x000000ff] ^ Td3[t[2] & 0x000000ff] ^ anSchedule[33];
            s[2] = Td0[t[2] >>> 24] ^ Td1[(t[1] >>> 16) & 0x000000ff] ^ Td2[(t[0] >>>  8) & 0x000000ff] ^ Td3[t[3] & 0x000000ff] ^ anSchedule[34];
            s[3] = Td0[t[3] >>> 24] ^ Td1[(t[2] >>> 16) & 0x000000ff] ^ Td2[(t[1] >>>  8) & 0x000000ff] ^ Td3[t[0] & 0x000000ff] ^ anSchedule[35];
            /* Round 9: */
            t[0] = Td0[s[0] >>> 24] ^ Td1[(s[3] >>> 16) & 0x000000ff] ^ Td2[(s[2] >>>  8) & 0x000000ff] ^ Td3[s[1] & 0x000000ff] ^ anSchedule[36];
            t[1] = Td0[s[1] >>> 24] ^ Td1[(s[0] >>> 16) & 0x000000ff] ^ Td2[(s[3] >>>  8) & 0x000000ff] ^ Td3[s[2] & 0x000000ff] ^ anSchedule[37];
            t[2] = Td0[s[2] >>> 24] ^ Td1[(s[1] >>> 16) & 0x000000ff] ^ Td2[(s[0] >>>  8) & 0x000000ff] ^ Td3[s[3] & 0x000000ff] ^ anSchedule[38];
            t[3] = Td0[s[3] >>> 24] ^ Td1[(s[2] >>> 16) & 0x000000ff] ^ Td2[(s[1] >>>  8) & 0x000000ff] ^ Td3[s[0] & 0x000000ff] ^ anSchedule[39];
            /* Round 10 is special: */
            s[0] = (Td4[(t[0] >>> 24)             ] & 0xff000000) ^
                   (Td4[(t[3] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Td4[(t[2] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Td4[(t[1]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[40];
            s[1] = (Td4[(t[1] >>> 24)             ] & 0xff000000) ^
                   (Td4[(t[0] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Td4[(t[3] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Td4[(t[2]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[41];
            s[2] = (Td4[(t[2] >>> 24)             ] & 0xff000000) ^
                   (Td4[(t[1] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Td4[(t[0] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Td4[(t[3]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[42];
            s[3] = (Td4[(t[3] >>> 24)             ] & 0xff000000) ^
                   (Td4[(t[2] >>> 16) & 0x000000ff] & 0x00ff0000) ^
                   (Td4[(t[1] >>>  8) & 0x000000ff] & 0x0000ff00) ^
                   (Td4[(t[0]       ) & 0x000000ff] & 0x000000ff) ^
                   anSchedule[43];

            /* Add previous cipher block: */
            s[0] ^= anInitVector[0];
            s[1] ^= anInitVector[1];
            s[2] ^= anInitVector[2];
            s[3] ^= anInitVector[3];

            /* Map plain state to output block: */
            PUTN(abPlain, nPlainStartIndex + 16 * i     , s[0]);
            PUTN(abPlain, nPlainStartIndex + 16 * i +  4, s[1]);
            PUTN(abPlain, nPlainStartIndex + 16 * i +  8, s[2]);
            PUTN(abPlain, nPlainStartIndex + 16 * i + 12, s[3]);

            /* Set previous cipher block for the next stage: */
            anInitVector[0] = GETN(abCipher, nCipherStartIndex + 16 * i     );
            anInitVector[1] = GETN(abCipher, nCipherStartIndex + 16 * i +  4);
            anInitVector[2] = GETN(abCipher, nCipherStartIndex + 16 * i +  8);
            anInitVector[3] = GETN(abCipher, nCipherStartIndex + 16 * i + 12);
        }
    }

    //////////////////////////////////////////////////////
    // 16-round SEED algorithm                          //
    // with 128-bit block length and 128-bit key length //
    //////////////////////////////////////////////////////

    private static int[/*16*/] KC = {
        0x9e3779b9, 0x3c6ef373, 0x78dde6e6, 0xf1bbcdcc,
        0xe3779b99, 0xc6ef3733, 0x8dde6e67, 0x1bbcdccf,
        0x3779b99e, 0x6ef3733c, 0xdde6e678, 0xbbcdccf1,
        0x779b99e3, 0xef3733c6, 0xde6e678d, 0xbcdccf1b
    };

    private static int[/*256*/] SS0 = {
        0x2989a1a8, 0x05858184, 0x16c6d2d4, 0x13c3d3d0,
        0x14445054, 0x1d0d111c, 0x2c8ca0ac, 0x25052124,
        0x1d4d515c, 0x03434340, 0x18081018, 0x1e0e121c,
        0x11415150, 0x3cccf0fc, 0x0acac2c8, 0x23436360,
        0x28082028, 0x04444044, 0x20002020, 0x1d8d919c,
        0x20c0e0e0, 0x22c2e2e0, 0x08c8c0c8, 0x17071314,
        0x2585a1a4, 0x0f8f838c, 0x03030300, 0x3b4b7378,
        0x3b8bb3b8, 0x13031310, 0x12c2d2d0, 0x2ecee2ec,
        0x30407070, 0x0c8c808c, 0x3f0f333c, 0x2888a0a8,
        0x32023230, 0x1dcdd1dc, 0x36c6f2f4, 0x34447074,
        0x2ccce0ec, 0x15859194, 0x0b0b0308, 0x17475354,
        0x1c4c505c, 0x1b4b5358, 0x3d8db1bc, 0x01010100,
        0x24042024, 0x1c0c101c, 0x33437370, 0x18889098,
        0x10001010, 0x0cccc0cc, 0x32c2f2f0, 0x19c9d1d8,
        0x2c0c202c, 0x27c7e3e4, 0x32427270, 0x03838380,
        0x1b8b9398, 0x11c1d1d0, 0x06868284, 0x09c9c1c8,
        0x20406060, 0x10405050, 0x2383a3a0, 0x2bcbe3e8,
        0x0d0d010c, 0x3686b2b4, 0x1e8e929c, 0x0f4f434c,
        0x3787b3b4, 0x1a4a5258, 0x06c6c2c4, 0x38487078,
        0x2686a2a4, 0x12021210, 0x2f8fa3ac, 0x15c5d1d4,
        0x21416160, 0x03c3c3c0, 0x3484b0b4, 0x01414140,
        0x12425250, 0x3d4d717c, 0x0d8d818c, 0x08080008,
        0x1f0f131c, 0x19899198, 0x00000000, 0x19091118,
        0x04040004, 0x13435350, 0x37c7f3f4, 0x21c1e1e0,
        0x3dcdf1fc, 0x36467274, 0x2f0f232c, 0x27072324,
        0x3080b0b0, 0x0b8b8388, 0x0e0e020c, 0x2b8ba3a8,
        0x2282a2a0, 0x2e4e626c, 0x13839390, 0x0d4d414c,
        0x29496168, 0x3c4c707c, 0x09090108, 0x0a0a0208,
        0x3f8fb3bc, 0x2fcfe3ec, 0x33c3f3f0, 0x05c5c1c4,
        0x07878384, 0x14041014, 0x3ecef2fc, 0x24446064,
        0x1eced2dc, 0x2e0e222c, 0x0b4b4348, 0x1a0a1218,
        0x06060204, 0x21012120, 0x2b4b6368, 0x26466264,
        0x02020200, 0x35c5f1f4, 0x12829290, 0x0a8a8288,
        0x0c0c000c, 0x3383b3b0, 0x3e4e727c, 0x10c0d0d0,
        0x3a4a7278, 0x07474344, 0x16869294, 0x25c5e1e4,
        0x26062224, 0x00808080, 0x2d8da1ac, 0x1fcfd3dc,
        0x2181a1a0, 0x30003030, 0x37073334, 0x2e8ea2ac,
        0x36063234, 0x15051114, 0x22022220, 0x38083038,
        0x34c4f0f4, 0x2787a3a4, 0x05454144, 0x0c4c404c,
        0x01818180, 0x29c9e1e8, 0x04848084, 0x17879394,
        0x35053134, 0x0bcbc3c8, 0x0ecec2cc, 0x3c0c303c,
        0x31417170, 0x11011110, 0x07c7c3c4, 0x09898188,
        0x35457174, 0x3bcbf3f8, 0x1acad2d8, 0x38c8f0f8,
        0x14849094, 0x19495158, 0x02828280, 0x04c4c0c4,
        0x3fcff3fc, 0x09494148, 0x39093138, 0x27476364,
        0x00c0c0c0, 0x0fcfc3cc, 0x17c7d3d4, 0x3888b0b8,
        0x0f0f030c, 0x0e8e828c, 0x02424240, 0x23032320,
        0x11819190, 0x2c4c606c, 0x1bcbd3d8, 0x2484a0a4,
        0x34043034, 0x31c1f1f0, 0x08484048, 0x02c2c2c0,
        0x2f4f636c, 0x3d0d313c, 0x2d0d212c, 0x00404040,
        0x3e8eb2bc, 0x3e0e323c, 0x3c8cb0bc, 0x01c1c1c0,
        0x2a8aa2a8, 0x3a8ab2b8, 0x0e4e424c, 0x15455154,
        0x3b0b3338, 0x1cccd0dc, 0x28486068, 0x3f4f737c,
        0x1c8c909c, 0x18c8d0d8, 0x0a4a4248, 0x16465254,
        0x37477374, 0x2080a0a0, 0x2dcde1ec, 0x06464244,
        0x3585b1b4, 0x2b0b2328, 0x25456164, 0x3acaf2f8,
        0x23c3e3e0, 0x3989b1b8, 0x3181b1b0, 0x1f8f939c,
        0x1e4e525c, 0x39c9f1f8, 0x26c6e2e4, 0x3282b2b0,
        0x31013130, 0x2acae2e8, 0x2d4d616c, 0x1f4f535c,
        0x24c4e0e4, 0x30c0f0f0, 0x0dcdc1cc, 0x08888088,
        0x16061214, 0x3a0a3238, 0x18485058, 0x14c4d0d4,
        0x22426260, 0x29092128, 0x07070304, 0x33033330,
        0x28c8e0e8, 0x1b0b1318, 0x05050104, 0x39497178,
        0x10809090, 0x2a4a6268, 0x2a0a2228, 0x1a8a9298
    };
    private static int[/*256*/] SS1 = {
        0x38380830, 0xe828c8e0, 0x2c2d0d21, 0xa42686a2,
        0xcc0fcfc3, 0xdc1eced2, 0xb03383b3, 0xb83888b0,
        0xac2f8fa3, 0x60204060, 0x54154551, 0xc407c7c3,
        0x44044440, 0x6c2f4f63, 0x682b4b63, 0x581b4b53,
        0xc003c3c3, 0x60224262, 0x30330333, 0xb43585b1,
        0x28290921, 0xa02080a0, 0xe022c2e2, 0xa42787a3,
        0xd013c3d3, 0x90118191, 0x10110111, 0x04060602,
        0x1c1c0c10, 0xbc3c8cb0, 0x34360632, 0x480b4b43,
        0xec2fcfe3, 0x88088880, 0x6c2c4c60, 0xa82888a0,
        0x14170713, 0xc404c4c0, 0x14160612, 0xf434c4f0,
        0xc002c2c2, 0x44054541, 0xe021c1e1, 0xd416c6d2,
        0x3c3f0f33, 0x3c3d0d31, 0x8c0e8e82, 0x98188890,
        0x28280820, 0x4c0e4e42, 0xf436c6f2, 0x3c3e0e32,
        0xa42585a1, 0xf839c9f1, 0x0c0d0d01, 0xdc1fcfd3,
        0xd818c8d0, 0x282b0b23, 0x64264662, 0x783a4a72,
        0x24270723, 0x2c2f0f23, 0xf031c1f1, 0x70324272,
        0x40024242, 0xd414c4d0, 0x40014141, 0xc000c0c0,
        0x70334373, 0x64274763, 0xac2c8ca0, 0x880b8b83,
        0xf437c7f3, 0xac2d8da1, 0x80008080, 0x1c1f0f13,
        0xc80acac2, 0x2c2c0c20, 0xa82a8aa2, 0x34340430,
        0xd012c2d2, 0x080b0b03, 0xec2ecee2, 0xe829c9e1,
        0x5c1d4d51, 0x94148490, 0x18180810, 0xf838c8f0,
        0x54174753, 0xac2e8ea2, 0x08080800, 0xc405c5c1,
        0x10130313, 0xcc0dcdc1, 0x84068682, 0xb83989b1,
        0xfc3fcff3, 0x7c3d4d71, 0xc001c1c1, 0x30310131,
        0xf435c5f1, 0x880a8a82, 0x682a4a62, 0xb03181b1,
        0xd011c1d1, 0x20200020, 0xd417c7d3, 0x00020202,
        0x20220222, 0x04040400, 0x68284860, 0x70314171,
        0x04070703, 0xd81bcbd3, 0x9c1d8d91, 0x98198991,
        0x60214161, 0xbc3e8eb2, 0xe426c6e2, 0x58194951,
        0xdc1dcdd1, 0x50114151, 0x90108090, 0xdc1cccd0,
        0x981a8a92, 0xa02383a3, 0xa82b8ba3, 0xd010c0d0,
        0x80018181, 0x0c0f0f03, 0x44074743, 0x181a0a12,
        0xe023c3e3, 0xec2ccce0, 0x8c0d8d81, 0xbc3f8fb3,
        0x94168692, 0x783b4b73, 0x5c1c4c50, 0xa02282a2,
        0xa02181a1, 0x60234363, 0x20230323, 0x4c0d4d41,
        0xc808c8c0, 0x9c1e8e92, 0x9c1c8c90, 0x383a0a32,
        0x0c0c0c00, 0x2c2e0e22, 0xb83a8ab2, 0x6c2e4e62,
        0x9c1f8f93, 0x581a4a52, 0xf032c2f2, 0x90128292,
        0xf033c3f3, 0x48094941, 0x78384870, 0xcc0cccc0,
        0x14150511, 0xf83bcbf3, 0x70304070, 0x74354571,
        0x7c3f4f73, 0x34350531, 0x10100010, 0x00030303,
        0x64244460, 0x6c2d4d61, 0xc406c6c2, 0x74344470,
        0xd415c5d1, 0xb43484b0, 0xe82acae2, 0x08090901,
        0x74364672, 0x18190911, 0xfc3ecef2, 0x40004040,
        0x10120212, 0xe020c0e0, 0xbc3d8db1, 0x04050501,
        0xf83acaf2, 0x00010101, 0xf030c0f0, 0x282a0a22,
        0x5c1e4e52, 0xa82989a1, 0x54164652, 0x40034343,
        0x84058581, 0x14140410, 0x88098981, 0x981b8b93,
        0xb03080b0, 0xe425c5e1, 0x48084840, 0x78394971,
        0x94178793, 0xfc3cccf0, 0x1c1e0e12, 0x80028282,
        0x20210121, 0x8c0c8c80, 0x181b0b13, 0x5c1f4f53,
        0x74374773, 0x54144450, 0xb03282b2, 0x1c1d0d11,
        0x24250521, 0x4c0f4f43, 0x00000000, 0x44064642,
        0xec2dcde1, 0x58184850, 0x50124252, 0xe82bcbe3,
        0x7c3e4e72, 0xd81acad2, 0xc809c9c1, 0xfc3dcdf1,
        0x30300030, 0x94158591, 0x64254561, 0x3c3c0c30,
        0xb43686b2, 0xe424c4e0, 0xb83b8bb3, 0x7c3c4c70,
        0x0c0e0e02, 0x50104050, 0x38390931, 0x24260622,
        0x30320232, 0x84048480, 0x68294961, 0x90138393,
        0x34370733, 0xe427c7e3, 0x24240420, 0xa42484a0,
        0xc80bcbc3, 0x50134353, 0x080a0a02, 0x84078783,
        0xd819c9d1, 0x4c0c4c40, 0x80038383, 0x8c0f8f83,
        0xcc0ecec2, 0x383b0b33, 0x480a4a42, 0xb43787b3
    };
    private static int[/*256*/] SS2 = {
        0xa1a82989, 0x81840585, 0xd2d416c6, 0xd3d013c3,
        0x50541444, 0x111c1d0d, 0xa0ac2c8c, 0x21242505,
        0x515c1d4d, 0x43400343, 0x10181808, 0x121c1e0e,
        0x51501141, 0xf0fc3ccc, 0xc2c80aca, 0x63602343,
        0x20282808, 0x40440444, 0x20202000, 0x919c1d8d,
        0xe0e020c0, 0xe2e022c2, 0xc0c808c8, 0x13141707,
        0xa1a42585, 0x838c0f8f, 0x03000303, 0x73783b4b,
        0xb3b83b8b, 0x13101303, 0xd2d012c2, 0xe2ec2ece,
        0x70703040, 0x808c0c8c, 0x333c3f0f, 0xa0a82888,
        0x32303202, 0xd1dc1dcd, 0xf2f436c6, 0x70743444,
        0xe0ec2ccc, 0x91941585, 0x03080b0b, 0x53541747,
        0x505c1c4c, 0x53581b4b, 0xb1bc3d8d, 0x01000101,
        0x20242404, 0x101c1c0c, 0x73703343, 0x90981888,
        0x10101000, 0xc0cc0ccc, 0xf2f032c2, 0xd1d819c9,
        0x202c2c0c, 0xe3e427c7, 0x72703242, 0x83800383,
        0x93981b8b, 0xd1d011c1, 0x82840686, 0xc1c809c9,
        0x60602040, 0x50501040, 0xa3a02383, 0xe3e82bcb,
        0x010c0d0d, 0xb2b43686, 0x929c1e8e, 0x434c0f4f,
        0xb3b43787, 0x52581a4a, 0xc2c406c6, 0x70783848,
        0xa2a42686, 0x12101202, 0xa3ac2f8f, 0xd1d415c5,
        0x61602141, 0xc3c003c3, 0xb0b43484, 0x41400141,
        0x52501242, 0x717c3d4d, 0x818c0d8d, 0x00080808,
        0x131c1f0f, 0x91981989, 0x00000000, 0x11181909,
        0x00040404, 0x53501343, 0xf3f437c7, 0xe1e021c1,
        0xf1fc3dcd, 0x72743646, 0x232c2f0f, 0x23242707,
        0xb0b03080, 0x83880b8b, 0x020c0e0e, 0xa3a82b8b,
        0xa2a02282, 0x626c2e4e, 0x93901383, 0x414c0d4d,
        0x61682949, 0x707c3c4c, 0x01080909, 0x02080a0a,
        0xb3bc3f8f, 0xe3ec2fcf, 0xf3f033c3, 0xc1c405c5,
        0x83840787, 0x10141404, 0xf2fc3ece, 0x60642444,
        0xd2dc1ece, 0x222c2e0e, 0x43480b4b, 0x12181a0a,
        0x02040606, 0x21202101, 0x63682b4b, 0x62642646,
        0x02000202, 0xf1f435c5, 0x92901282, 0x82880a8a,
        0x000c0c0c, 0xb3b03383, 0x727c3e4e, 0xd0d010c0,
        0x72783a4a, 0x43440747, 0x92941686, 0xe1e425c5,
        0x22242606, 0x80800080, 0xa1ac2d8d, 0xd3dc1fcf,
        0xa1a02181, 0x30303000, 0x33343707, 0xa2ac2e8e,
        0x32343606, 0x11141505, 0x22202202, 0x30383808,
        0xf0f434c4, 0xa3a42787, 0x41440545, 0x404c0c4c,
        0x81800181, 0xe1e829c9, 0x80840484, 0x93941787,
        0x31343505, 0xc3c80bcb, 0xc2cc0ece, 0x303c3c0c,
        0x71703141, 0x11101101, 0xc3c407c7, 0x81880989,
        0x71743545, 0xf3f83bcb, 0xd2d81aca, 0xf0f838c8,
        0x90941484, 0x51581949, 0x82800282, 0xc0c404c4,
        0xf3fc3fcf, 0x41480949, 0x31383909, 0x63642747,
        0xc0c000c0, 0xc3cc0fcf, 0xd3d417c7, 0xb0b83888,
        0x030c0f0f, 0x828c0e8e, 0x42400242, 0x23202303,
        0x91901181, 0x606c2c4c, 0xd3d81bcb, 0xa0a42484,
        0x30343404, 0xf1f031c1, 0x40480848, 0xc2c002c2,
        0x636c2f4f, 0x313c3d0d, 0x212c2d0d, 0x40400040,
        0xb2bc3e8e, 0x323c3e0e, 0xb0bc3c8c, 0xc1c001c1,
        0xa2a82a8a, 0xb2b83a8a, 0x424c0e4e, 0x51541545,
        0x33383b0b, 0xd0dc1ccc, 0x60682848, 0x737c3f4f,
        0x909c1c8c, 0xd0d818c8, 0x42480a4a, 0x52541646,
        0x73743747, 0xa0a02080, 0xe1ec2dcd, 0x42440646,
        0xb1b43585, 0x23282b0b, 0x61642545, 0xf2f83aca,
        0xe3e023c3, 0xb1b83989, 0xb1b03181, 0x939c1f8f,
        0x525c1e4e, 0xf1f839c9, 0xe2e426c6, 0xb2b03282,
        0x31303101, 0xe2e82aca, 0x616c2d4d, 0x535c1f4f,
        0xe0e424c4, 0xf0f030c0, 0xc1cc0dcd, 0x80880888,
        0x12141606, 0x32383a0a, 0x50581848, 0xd0d414c4,
        0x62602242, 0x21282909, 0x03040707, 0x33303303,
        0xe0e828c8, 0x13181b0b, 0x01040505, 0x71783949,
        0x90901080, 0x62682a4a, 0x22282a0a, 0x92981a8a
    };
    private static int[/*256*/] SS3 = {
        0x08303838, 0xc8e0e828, 0x0d212c2d, 0x86a2a426,
        0xcfc3cc0f, 0xced2dc1e, 0x83b3b033, 0x88b0b838,
        0x8fa3ac2f, 0x40606020, 0x45515415, 0xc7c3c407,
        0x44404404, 0x4f636c2f, 0x4b63682b, 0x4b53581b,
        0xc3c3c003, 0x42626022, 0x03333033, 0x85b1b435,
        0x09212829, 0x80a0a020, 0xc2e2e022, 0x87a3a427,
        0xc3d3d013, 0x81919011, 0x01111011, 0x06020406,
        0x0c101c1c, 0x8cb0bc3c, 0x06323436, 0x4b43480b,
        0xcfe3ec2f, 0x88808808, 0x4c606c2c, 0x88a0a828,
        0x07131417, 0xc4c0c404, 0x06121416, 0xc4f0f434,
        0xc2c2c002, 0x45414405, 0xc1e1e021, 0xc6d2d416,
        0x0f333c3f, 0x0d313c3d, 0x8e828c0e, 0x88909818,
        0x08202828, 0x4e424c0e, 0xc6f2f436, 0x0e323c3e,
        0x85a1a425, 0xc9f1f839, 0x0d010c0d, 0xcfd3dc1f,
        0xc8d0d818, 0x0b23282b, 0x46626426, 0x4a72783a,
        0x07232427, 0x0f232c2f, 0xc1f1f031, 0x42727032,
        0x42424002, 0xc4d0d414, 0x41414001, 0xc0c0c000,
        0x43737033, 0x47636427, 0x8ca0ac2c, 0x8b83880b,
        0xc7f3f437, 0x8da1ac2d, 0x80808000, 0x0f131c1f,
        0xcac2c80a, 0x0c202c2c, 0x8aa2a82a, 0x04303434,
        0xc2d2d012, 0x0b03080b, 0xcee2ec2e, 0xc9e1e829,
        0x4d515c1d, 0x84909414, 0x08101818, 0xc8f0f838,
        0x47535417, 0x8ea2ac2e, 0x08000808, 0xc5c1c405,
        0x03131013, 0xcdc1cc0d, 0x86828406, 0x89b1b839,
        0xcff3fc3f, 0x4d717c3d, 0xc1c1c001, 0x01313031,
        0xc5f1f435, 0x8a82880a, 0x4a62682a, 0x81b1b031,
        0xc1d1d011, 0x00202020, 0xc7d3d417, 0x02020002,
        0x02222022, 0x04000404, 0x48606828, 0x41717031,
        0x07030407, 0xcbd3d81b, 0x8d919c1d, 0x89919819,
        0x41616021, 0x8eb2bc3e, 0xc6e2e426, 0x49515819,
        0xcdd1dc1d, 0x41515011, 0x80909010, 0xccd0dc1c,
        0x8a92981a, 0x83a3a023, 0x8ba3a82b, 0xc0d0d010,
        0x81818001, 0x0f030c0f, 0x47434407, 0x0a12181a,
        0xc3e3e023, 0xcce0ec2c, 0x8d818c0d, 0x8fb3bc3f,
        0x86929416, 0x4b73783b, 0x4c505c1c, 0x82a2a022,
        0x81a1a021, 0x43636023, 0x03232023, 0x4d414c0d,
        0xc8c0c808, 0x8e929c1e, 0x8c909c1c, 0x0a32383a,
        0x0c000c0c, 0x0e222c2e, 0x8ab2b83a, 0x4e626c2e,
        0x8f939c1f, 0x4a52581a, 0xc2f2f032, 0x82929012,
        0xc3f3f033, 0x49414809, 0x48707838, 0xccc0cc0c,
        0x05111415, 0xcbf3f83b, 0x40707030, 0x45717435,
        0x4f737c3f, 0x05313435, 0x00101010, 0x03030003,
        0x44606424, 0x4d616c2d, 0xc6c2c406, 0x44707434,
        0xc5d1d415, 0x84b0b434, 0xcae2e82a, 0x09010809,
        0x46727436, 0x09111819, 0xcef2fc3e, 0x40404000,
        0x02121012, 0xc0e0e020, 0x8db1bc3d, 0x05010405,
        0xcaf2f83a, 0x01010001, 0xc0f0f030, 0x0a22282a,
        0x4e525c1e, 0x89a1a829, 0x46525416, 0x43434003,
        0x85818405, 0x04101414, 0x89818809, 0x8b93981b,
        0x80b0b030, 0xc5e1e425, 0x48404808, 0x49717839,
        0x87939417, 0xccf0fc3c, 0x0e121c1e, 0x82828002,
        0x01212021, 0x8c808c0c, 0x0b13181b, 0x4f535c1f,
        0x47737437, 0x44505414, 0x82b2b032, 0x0d111c1d,
        0x05212425, 0x4f434c0f, 0x00000000, 0x46424406,
        0xcde1ec2d, 0x48505818, 0x42525012, 0xcbe3e82b,
        0x4e727c3e, 0xcad2d81a, 0xc9c1c809, 0xcdf1fc3d,
        0x00303030, 0x85919415, 0x45616425, 0x0c303c3c,
        0x86b2b436, 0xc4e0e424, 0x8bb3b83b, 0x4c707c3c,
        0x0e020c0e, 0x40505010, 0x09313839, 0x06222426,
        0x02323032, 0x84808404, 0x49616829, 0x83939013,
        0x07333437, 0xc7e3e427, 0x04202424, 0x84a0a424,
        0xcbc3c80b, 0x43535013, 0x0a02080a, 0x87838407,
        0xc9d1d819, 0x4c404c0c, 0x83838003, 0x8f838c0f,
        0xcec2cc0e, 0x0b33383b, 0x4a42480a, 0x87b3b437
    };

    /* 
        Internal module for expansion of key into key schedule
        @ Input : anSessionKey : initial key
                  anSchedule : buffer for expanded key schedule
        @ Output: anSchedule : holds expanded key schedule
        @ Return: none
    */
    public static void SEEDKeyExpansionV20
    (int[/*4*/] anSessionKey, int[/*32*/] anSchedule)
    {
        int a, b, c, d, t0, t1;
        int i;

        a = anSessionKey[0];
        b = anSessionKey[1];
        c = anSessionKey[2];
        d = anSessionKey[3];

        /* 0 and 1: */
        t0 = a + c - KC[0];
        t1 = b - d + KC[0];
        anSchedule[0] = SS0[(t0       ) & 0x000000ff] ^
                        SS1[(t0 >>>  8) & 0x000000ff] ^
                        SS2[(t0 >>> 16) & 0x000000ff] ^
                        SS3[(t0 >>> 24) & 0x000000ff];
        anSchedule[1] = SS0[(t1       ) & 0x000000ff] ^
                        SS1[(t1 >>>  8) & 0x000000ff] ^
                        SS2[(t1 >>> 16) & 0x000000ff] ^
                        SS3[(t1 >>> 24) & 0x000000ff];
        /* From 2 to 29: */
        for (i = 0; i < 7; ++i) {
            t0 = a;
            a = (a >>> 8) ^ ( b << 24);
            b = (b >>> 8) ^ (t0 << 24);
            t0 = a + c - KC[2 * i + 1];
            t1 = b + KC[2 * i + 1] - d;
            anSchedule[4 * i +  2] = SS0[(t0       ) & 0x000000ff] ^
                                     SS1[(t0 >>>  8) & 0x000000ff] ^
                                     SS2[(t0 >>> 16) & 0x000000ff] ^
                                     SS3[(t0 >>> 24) & 0x000000ff];
            anSchedule[4 * i +  3] = SS0[(t1       ) & 0x000000ff] ^
                                     SS1[(t1 >>>  8) & 0x000000ff] ^
                                     SS2[(t1 >>> 16) & 0x000000ff] ^
                                     SS3[(t1 >>> 24) & 0x000000ff];
            t0 = c;
            c = (c << 8) ^ ( d >>> 24);
            d = (d << 8) ^ (t0 >>> 24);
            t0 = a + c - KC[2 * i + 2];
            t1 = b + KC[2 * i + 2] - d;
            anSchedule[4 * i +  4] = SS0[(t0       ) & 0x000000ff] ^
                                     SS1[(t0 >>>  8) & 0x000000ff] ^
                                     SS2[(t0 >>> 16) & 0x000000ff] ^
                                     SS3[(t0 >>> 24) & 0x000000ff];
            anSchedule[4 * i +  5] = SS0[(t1       ) & 0x000000ff] ^
                                     SS1[(t1 >>>  8) & 0x000000ff] ^
                                     SS2[(t1 >>> 16) & 0x000000ff] ^
                                     SS3[(t1 >>> 24) & 0x000000ff];
        }
        /* 30 and 31: */
        t0 = a;
        a = (a >>> 8) ^ ( b << 24);
        b = (b >>> 8) ^ (t0 << 24);
        t0 = a + c - KC[15];
        t1 = b + KC[15] - d;
        anSchedule[30] = SS0[(t0       ) & 0x000000ff] ^
                         SS1[(t0 >>>  8) & 0x000000ff] ^
                         SS2[(t0 >>> 16) & 0x000000ff] ^
                         SS3[(t0 >>> 24) & 0x000000ff];
        anSchedule[31] = SS0[(t1       ) & 0x000000ff] ^
                         SS1[(t1 >>>  8) & 0x000000ff] ^
                         SS2[(t1 >>> 16) & 0x000000ff] ^
                         SS3[(t1 >>> 24) & 0x000000ff];
    }

    /*
        Internal module for block encryption in CBC-mode operation
        @ Input : abPlain : array of plain text bytes
                  nPlainStartIndex : index in "abPlain" from which to start encryption
                  nPlainBytes : # of bytes to be encrypted; arbitrary positive length
                  anSchedule : expanded encryption key schedule
                  anInitVector : initialization vector for CBC-mode operation
                  abCipher : buffer for cipher text bytes; assumed to have sufficient length
                  nCipherStartIndex : index in "abCipher" from which to write encrypted text bytes
        @ Output: anInitVector : contains the last cipher block
                  abCipher : contains all the cipher blocks (as bytes)
        @ Return: # of 16-byte blocks that have been encrypted
    */
    private static int SEEDCBCEncryptV20
    (byte[] abPlain,
     int nPlainStartIndex,
     int nPlainBytes,
     int[/*32*/] anSchedule,
     int[/*4*/] anInitVector,
     byte[] abCipher,
     int nCipherStartIndex)
    {
        int nBlockNum, i, j;
        int l0, l1, r0, r1, t0 = 0, t1 = 0;

        /* # of 16-byte blocks to be encrypted */
        nBlockNum = (nPlainBytes % 16 == 0) ? (nPlainBytes / 16) : (nPlainBytes / 16 + 1);

        for (i = 0; i < nBlockNum; ++i) {
            /* Previous cipher block ^ current plain block: */
            if ((nPlainBytes % 16 != 0) && (i == nBlockNum - 1)) {
                for (j = 16 * i; j < nPlainBytes; ++j)
                    abCipher[nCipherStartIndex + j] = abPlain[nPlainStartIndex + j];
            } else {
                for (j = 16 * i; j < 16 * (i + 1); ++j)
                    abCipher[nCipherStartIndex + j] = abPlain[nPlainStartIndex + j];
            }
            l0 = anInitVector[0] ^ GETN(abCipher, nCipherStartIndex + 16 * i     );
            l1 = anInitVector[1] ^ GETN(abCipher, nCipherStartIndex + 16 * i +  4);
            r0 = anInitVector[2] ^ GETN(abCipher, nCipherStartIndex + 16 * i +  8);
            r1 = anInitVector[3] ^ GETN(abCipher, nCipherStartIndex + 16 * i + 12);

            /* 16 rounds: */
            for (j = 0; j < 8; ++j) {
                /* (2 * j + 1)-th round: */
                t0 = r0 ^ anSchedule[4 * j    ];
                t1 = r1 ^ anSchedule[4 * j + 1];
                t1 ^= t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                t0 = SS0[(t0       ) & 0x000000ff] ^
                     SS1[(t0 >>>  8) & 0x000000ff] ^
                     SS2[(t0 >>> 16) & 0x000000ff] ^
                     SS3[(t0 >>> 24) & 0x000000ff];
                t1 += t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                l0 ^= t0;
                l1 ^= t1;
                /* (2 * j + 2)-th round: */
                t0 = l0 ^ anSchedule[4 * j + 2];
                t1 = l1 ^ anSchedule[4 * j + 3];
                t1 ^= t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                t0 = SS0[(t0       ) & 0x000000ff] ^
                     SS1[(t0 >>>  8) & 0x000000ff] ^
                     SS2[(t0 >>> 16) & 0x000000ff] ^
                     SS3[(t0 >>> 24) & 0x000000ff];
                t1 += t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                r0 ^= t0;
                r1 ^= t1;
            }

            /* Map cipher state to initialization vector and output block: */
            anInitVector[0] = r0;
            anInitVector[1] = r1;
            anInitVector[2] = l0;
            anInitVector[3] = l1;
            PUTN(abCipher, nCipherStartIndex + 16 * i     , r0);
            PUTN(abCipher, nCipherStartIndex + 16 * i +  4, r1);
            PUTN(abCipher, nCipherStartIndex + 16 * i +  8, l0);
            PUTN(abCipher, nCipherStartIndex + 16 * i + 12, l1);
        }

        return nBlockNum;
    }

    /*
        Internal module for block decryption in CBC-mode operation
        @ Input : abCipher : array of cipher text bytes
                  nCipherStartIndex : index in "abCipher" from which to start decryption
                  nCipherBlocks : # of blocks to be decrypted; multiple of 16
                  anSchedule : expanded decryption key schedule
                  anInitVector : initialization vector for CBC-mode operation
                  abPlain : buffer for plain text bytes; assumed to have sufficient length
                  nPlainStartIndex : index in "abPlain" from which to write decrypted text bytes
        @ Output: anInitVector : contains the last cipher block
                  abCipher : contains all the plain blocks (as bytes)
        @ Return: none
    */
    private static void SEEDCBCDecryptV20
    (byte[] abCipher,
     int nCipherStartIndex,
     int nCipherBlocks,
     int[/*32*/] anSchedule,
     int[/*4*/] anInitVector,
     byte[] abPlain,
     int nPlainStartIndex)
    {
        int i, j;
        int l0, l1, r0, r1, t0 = 0, t1 = 0;

        for (i = 0; i < nCipherBlocks; ++i) {
            /* Current cipher block: */
            l0 = GETN(abCipher, nCipherStartIndex + 16 * i     );
            l1 = GETN(abCipher, nCipherStartIndex + 16 * i +  4);
            r0 = GETN(abCipher, nCipherStartIndex + 16 * i +  8);
            r1 = GETN(abCipher, nCipherStartIndex + 16 * i + 12);

            /* 16 rounds: */
            for (j = 7; j >= 0; --j) {
                /* (15 - 2 * j)-th round: */
                t0 = r0 ^ anSchedule[4 * j + 2];
                t1 = r1 ^ anSchedule[4 * j + 3];
                t1 ^= t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                t0 = SS0[(t0       ) & 0x000000ff] ^
                     SS1[(t0 >>>  8) & 0x000000ff] ^
                     SS2[(t0 >>> 16) & 0x000000ff] ^
                     SS3[(t0 >>> 24) & 0x000000ff];
                t1 += t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                l0 ^= t0;
                l1 ^= t1;
                /* (16 - 2 * j)-th round: */
                t0 = l0 ^ anSchedule[4 * j    ];
                t1 = l1 ^ anSchedule[4 * j + 1];
                t1 ^= t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                t0 = SS0[(t0       ) & 0x000000ff] ^
                     SS1[(t0 >>>  8) & 0x000000ff] ^
                     SS2[(t0 >>> 16) & 0x000000ff] ^
                     SS3[(t0 >>> 24) & 0x000000ff];
                t1 += t0;
                t1 = SS0[(t1       ) & 0x000000ff] ^
                     SS1[(t1 >>>  8) & 0x000000ff] ^
                     SS2[(t1 >>> 16) & 0x000000ff] ^
                     SS3[(t1 >>> 24) & 0x000000ff];
                t0 += t1;
                r0 ^= t0;
                r1 ^= t1;
            }

            /* Add previous cipher block: */
            r0 ^= anInitVector[0];
            r1 ^= anInitVector[1];
            l0 ^= anInitVector[2];
            l1 ^= anInitVector[3];

            /* Map plain state to output block: */
            PUTN(abPlain, nPlainStartIndex + 16 * i     , r0);
            PUTN(abPlain, nPlainStartIndex + 16 * i +  4, r1);
            PUTN(abPlain, nPlainStartIndex + 16 * i +  8, l0);
            PUTN(abPlain, nPlainStartIndex + 16 * i + 12, l1);

            /* Set previous cipher block for the next stage: */
            anInitVector[0] = GETN(abCipher, nCipherStartIndex + 16 * i     );
            anInitVector[1] = GETN(abCipher, nCipherStartIndex + 16 * i +  4);
            anInitVector[2] = GETN(abCipher, nCipherStartIndex + 16 * i +  8);
            anInitVector[3] = GETN(abCipher, nCipherStartIndex + 16 * i + 12);
        }
    }

    //////////////////////////////////////////////////////
    // General                                          //
    //////////////////////////////////////////////////////

    private static int BUFFSIZE = 1048576; /* 1M bytes */

    /*
        Internal module for converting byte array to hexadecimal array
        @ Input : ab : cipher text bytes
                  nabstartindex : index in "ab" from which to read
                  ablen : # of bytes in "ab"
                  ax : buffer for converted hexadecimal array
                  naxstartindex : index in "ax" from which to write
        @ Output: ax : holds converted hexadecimal array
        @ Return: none
    */
    private static void ab2ax
    (byte[] ab,
     int nabstartindex,
     int ablen,
     byte[] ax,
     int naxstartindex)
    {
        int i, hi, lo;

        for (i = 0; i < ablen; ++i) {
            hi = ((int)(ab[nabstartindex + i]) >>> 4) & 0x0000000f;
            lo = ab[nabstartindex + i] & 0x0000000f;
            ax[naxstartindex + 2 * i] = (hi < 10) ? (byte)(hi + '0') : (byte)(hi - 10 + 'a');
            ax[naxstartindex + 2 * i + 1] = (lo < 10) ? (byte)(lo + '0') : (byte)(lo - 10 + 'a');
        }
    }

    /*
        Internal module for converting hexadecimal array to byte array
        @ Input : ax : hexadecimal array of cipher text bytes
                  naxstartindex : index in "ax" from which to read
                  ablen : # of bytes in "ab"
                  ab : buffer for converted cipher text bytes
                  nabstartindex : index in "ab" from which to write
        @ Output: ab : holds converted cipher text bytes
        @ Return: 1 ==> successful
                  0 ==> "ax" contains invalid hexadecimal character
    */
    private static int ax2ab
    (byte[] ax,
     int naxstartindex,
     int ablen,
     byte[] ab,
     int nabstartindex)
    {
        int i, hi, lo;

        for (i = 0; i < ablen; ++i) {
            if (ax[naxstartindex + 2 * i] >= '0' && ax[naxstartindex + 2 * i] <= '9')
                hi = (int)(ax[naxstartindex + 2 * i] - '0');
            else if (ax[naxstartindex + 2 * i] >= 'a' && ax[naxstartindex + 2 * i] <= 'f')
                hi = (int)(ax[naxstartindex + 2 * i] - 'a' + 10);
            else
                return 0;
            if (ax[naxstartindex + 2 * i + 1] >= '0' && ax[naxstartindex + 2 * i + 1] <= '9')
                lo = (int)(ax[naxstartindex + 2 * i + 1] - '0');
            else if (ax[naxstartindex + 2 * i + 1] >= 'a' && ax[naxstartindex + 2 * i + 1] <= 'f')
                lo = (int)(ax[naxstartindex + 2 * i + 1] - 'a' + 10);
            else
                return 0;
            ab[nabstartindex + i] = (byte)((hi << 4) ^ lo);
        }

        return 1;
    }

    /*
        Internal module for generation of header
        @ Input : algorithm : 1 ==> "Rijndael"
                  nPlainBytes : # of bytes to be encrypted
                  passphrase : user passphrase
                  sessionkey : buffer for random session key
                  abHeader : buffer for header
                  nStartIndex : index in "abHeader" from which to write 4 header blocks
        @ Output: sessionkey : holds random session key
                  abHeader : holds header
        @ Return: none
    */
    private static void MakeHeaderV20
    (int algorithm,
     int nPlainBytes,
     byte[] passphrase,
     int[/*4*/] sessionkey,
     byte[/*4*16*/] abHeader,
     int nStartIndex)
    {
        int i;
        int[] userkey = new int[4];
        int[/*4*/] masterkey = {0x91c5c554, 0x5491c5c5, 0xc55491c5, 0xc5c55491};
        int[] Schedule = new int[4*11];
        int[] InitVector = new int[4];
        byte[] temp = new byte[16];

        /* Generate random session key: */
        //date 로 셋션 키 생성
       //for (i = 0; i < 4; ++i)
         //  sessionkey[i] = rand.nextInt();
        sessionkey[0] = 0x91c5c554;
        sessionkey[1] = 0x5491c5c5;
        sessionkey[2] = 0xc55491c5;
        sessionkey[3] = 0xc5c55491;


        /* Generate MD4 hash of user passphrase to be used as user key: */
        MD4hash(passphrase, userkey);

        /* 1st 16-byte block ==> header string: */
        abHeader[nStartIndex +  0] = (byte)'-';
        abHeader[nStartIndex +  1] = (byte)'-';
        abHeader[nStartIndex +  2] = (byte)'B';
        abHeader[nStartIndex +  3] = (byte)'E';
        abHeader[nStartIndex +  4] = (byte)'G';
        abHeader[nStartIndex +  5] = (byte)'I';
        abHeader[nStartIndex +  6] = (byte)'N';
        abHeader[nStartIndex +  7] = (byte)' ';
        abHeader[nStartIndex +  8] = (byte)'C';
        abHeader[nStartIndex +  9] = (byte)'I';
        abHeader[nStartIndex + 10] = (byte)'P';
        abHeader[nStartIndex + 11] = (byte)'H';
        abHeader[nStartIndex + 12] = (byte)'E';
        abHeader[nStartIndex + 13] = (byte)'R';
        abHeader[nStartIndex + 14] = (byte)'-';
        abHeader[nStartIndex + 15] = (byte)'-';

        /* 2nd 16-byte block ==> general information: */
        abHeader[16] = (byte)'1'; /* Version 1 */
        switch (algorithm) {
            case 1:
                abHeader[17] = (byte)'1';
                break;
            case 2:
                abHeader[17] = (byte)'2';
                break;
            default:
                break;
        } /* Algorithm : '1' ==> Rijndael, '2' ==> SEED */
        abHeader[18] = (byte)'1'; /* Hash algorithm : '1' ==> MD4 */
        abHeader[19] = (byte)'1'; /* Mode of operation : '1' ==> CBC */
        PUTN(abHeader, nStartIndex + 20, nPlainBytes); /* # of bytes to be encrypted */
        for (i = 24; i < 32; ++i)
            abHeader[i] = (byte)'0'; /* Blank 8 bytes */

        /* 3rd 16-byte block ==> session key encrypted by user key: */
        PUTN(temp,  0, sessionkey[0]);
        PUTN(temp,  4, sessionkey[1]);
        PUTN(temp,  8, sessionkey[2]);
        PUTN(temp, 12, sessionkey[3]);

         for (i = 0; i < 4; ++i)
            InitVector[i] = 0x00000000;
        switch (algorithm) {
            case 1:
                RijndaelEncKeyExpansionV20(userkey, Schedule);
                RijndaelCBCEncryptV20(temp, 0, 16, Schedule, InitVector, abHeader, nStartIndex + 32);
                break;
            case 2:
                SEEDKeyExpansionV20(userkey, Schedule);
                SEEDCBCEncryptV20(temp, 0, 16, Schedule, InitVector, abHeader, nStartIndex + 32);
                break;
            default:
                break;
        }


        /* 4th 16-byte block ==> session key encrypted by master key: */
        for (i = 0; i < 4; ++i)
            InitVector[i] = 0x00000000;
        switch (algorithm) {
            case 1:
                RijndaelEncKeyExpansionV20(masterkey, Schedule);
                RijndaelCBCEncryptV20(temp, 0, 16, Schedule, InitVector, abHeader, nStartIndex + 48);
                break;
            case 2:
                SEEDKeyExpansionV20(masterkey, Schedule);
                SEEDCBCEncryptV20(temp, 0, 16, Schedule, InitVector, abHeader, nStartIndex + 48);
                break;
            default:
                break;
        }
    }

    /*
        Internal module for generation of footer
        @ Input : abFooter : buffer for footer
                  nStartIndex : index in "abFooter" from which to write 1 header block
        @ Output: abFooter : holds footer
        @ Return: none
    */
    private static void MakeFooterV20
    (byte[/*16*/] abFooter, int nStartIndex)
    {
        /* Footer string: */
        abFooter[nStartIndex +  0] = (byte)'0';
        abFooter[nStartIndex +  1] = (byte)'0';
        abFooter[nStartIndex +  2] = (byte)'-';
        abFooter[nStartIndex +  3] = (byte)'-';
        abFooter[nStartIndex +  4] = (byte)'E';
        abFooter[nStartIndex +  5] = (byte)'N';
        abFooter[nStartIndex +  6] = (byte)'D';
        abFooter[nStartIndex +  7] = (byte)' ';
        abFooter[nStartIndex +  8] = (byte)'C';
        abFooter[nStartIndex +  9] = (byte)'I';
        abFooter[nStartIndex + 10] = (byte)'P';
        abFooter[nStartIndex + 11] = (byte)'H';
        abFooter[nStartIndex + 12] = (byte)'E';
        abFooter[nStartIndex + 13] = (byte)'R';
        abFooter[nStartIndex + 14] = (byte)'-';
        abFooter[nStartIndex + 15] = (byte)'-';
    }

    /*
        Internal module for getting length (in bytes) of cipher text
        @ Input : plainlength : # of bytes to be encrypted
        @ Output: none
        @ Return: 0 <== if plainlength <= 0
                  length (in bytes) of cipher text <== if plainlength > 0
    */
    private static int GetCipherLengthV20
    (int plainlength)
    {
        if (plainlength <= 0)
            return 0;
        else
            return (plainlength % 16 != 0) ?
                   (4 * 16 + (plainlength / 16 + 1) * 16 + 16) :
                   (4 * 16 + plainlength + 16);
    }

    /* 
        Internal module for checking integrity of cipher data
        @ Input : abHeader : header contents
                  nHeaderStartIndex : index in "abHeader" from which header starts
                  abFooter : footer contents
                  nFooterStartIndex : index in "abFooter" from which footer starts
                  nCipherBlockNum : # of 16-byte blocks in body
                  passphrase : user input passphrase whose correctness should be checked
                  anSessionKey : buffer for extracted session key
                  algorithm : reference to buffer for algorithm code
        @ Output: anSessionKey : holds extracted session key
                  algorithm : refers to algorithm code
        @ Return: -1 <== if data is invalid
                  0 <== if data is valid but passphrase is incorrect
                  # of plain text bytes <== if data is valid and passphrase is correct
    */
    private static int CipherStatCheckV20
    (byte[/*4*16*/] abHeader,
     int nHeaderStartIndex,
     byte[/*16*/] abFooter,
     int nFooterStartIndex,
     int nCipherBlockNum,
     byte[] passphrase,
     int[/*4*/] anSessionKey,
     int[/*1*/] algorithm)
    {
        int i;

        /* Footer string check: */
        if (abFooter[nFooterStartIndex     ] != '0') return -1;
        if (abFooter[nFooterStartIndex +  1] != '0') return -1;
        if (abFooter[nFooterStartIndex +  2] != '-') return -1;
        if (abFooter[nFooterStartIndex +  3] != '-') return -1;
        if (abFooter[nFooterStartIndex +  4] != 'E') return -1;
        if (abFooter[nFooterStartIndex +  5] != 'N') return -1;
        if (abFooter[nFooterStartIndex +  6] != 'D') return -1;
        if (abFooter[nFooterStartIndex +  7] != ' ') return -1;
        if (abFooter[nFooterStartIndex +  8] != 'C') return -1;
        if (abFooter[nFooterStartIndex +  9] != 'I') return -1;
        if (abFooter[nFooterStartIndex + 10] != 'P') return -1;
        if (abFooter[nFooterStartIndex + 11] != 'H') return -1;
        if (abFooter[nFooterStartIndex + 12] != 'E') return -1;
        if (abFooter[nFooterStartIndex + 13] != 'R') return -1;
        if (abFooter[nFooterStartIndex + 14] != '-') return -1;
        if (abFooter[nFooterStartIndex + 15] != '-') return -1;

        /* 1st header block check: */
        if (abHeader[nHeaderStartIndex     ] != '-') return -1;
        if (abHeader[nHeaderStartIndex +  1] != '-') return -1;
        if (abHeader[nHeaderStartIndex +  2] != 'B') return -1;
        if (abHeader[nHeaderStartIndex +  3] != 'E') return -1;
        if (abHeader[nHeaderStartIndex +  4] != 'G') return -1;
        if (abHeader[nHeaderStartIndex +  5] != 'I') return -1;
        if (abHeader[nHeaderStartIndex +  6] != 'N') return -1;
        if (abHeader[nHeaderStartIndex +  7] != ' ') return -1;
        if (abHeader[nHeaderStartIndex +  8] != 'C') return -1;
        if (abHeader[nHeaderStartIndex +  9] != 'I') return -1;
        if (abHeader[nHeaderStartIndex + 10] != 'P') return -1;
        if (abHeader[nHeaderStartIndex + 11] != 'H') return -1;
        if (abHeader[nHeaderStartIndex + 12] != 'E') return -1;
        if (abHeader[nHeaderStartIndex + 13] != 'R') return -1;
        if (abHeader[nHeaderStartIndex + 14] != '-') return -1;
        if (abHeader[nHeaderStartIndex + 15] != '-') return -1;

        /* Version check: */
        if (abHeader[nHeaderStartIndex + 16] != '1')
            return -1;
        /* Algorithm check: */
        if (abHeader[nHeaderStartIndex + 17] != '1' && abHeader[nHeaderStartIndex + 17] != '2')
            return -1;
        algorithm[0] = (int)(abHeader[nHeaderStartIndex + 17] - '0');
        /* Hash algoritm check: */
        if (abHeader[nHeaderStartIndex + 18] != '1')
            return -1;
        /* Mode of operation check: */
        if (abHeader[nHeaderStartIndex + 19] != '1')
            return -1;

        /* Original file size compatibility check: */
        int nPlainBytes = GETN(abHeader, nHeaderStartIndex + 20);
        int n = (nPlainBytes % 16 != 0) ? (nPlainBytes / 16 + 1) : (nPlainBytes / 16);
        if (n != nCipherBlockNum)
            return -1;

        /* Blank 8-bytes check: */
        for (i = 24; i < 32; ++i) {
            if (abHeader[nHeaderStartIndex + i] != '0')
                return -1;
        }

        /* Passphrase correctness check: */
        byte[] s = new byte[16];
        byte[] t = new byte[16];
        int[] key = new int[4];
        int[/*4*/] masterkey = {0x91c5c554, 0x5491c5c5, 0xc55491c5, 0xc5c55491};
        int[] Schedule = new int[4*11];
        int[] InitVector = new int[4];
        switch (algorithm[0]) {
            case 1:
                MD4hash(passphrase, key);
                RijndaelDecKeyExpansionV20(key, Schedule);
                for (i = 0; i < 4; ++i)
                    InitVector[i] = 0x00000000;
                RijndaelCBCDecryptV20(abHeader, nHeaderStartIndex + 32, 1, Schedule, InitVector, s, 0);

                RijndaelDecKeyExpansionV20(masterkey, Schedule);
                for (i = 0; i < 4; ++i)
                    InitVector[i] = 0x00000000;
                RijndaelCBCDecryptV20(abHeader, nHeaderStartIndex + 48, 1, Schedule, InitVector, t, 0);
                break;
            case 2:
                MD4hash(passphrase, key);
                SEEDKeyExpansionV20(key, Schedule);
                for (i = 0; i < 4; ++i)
                    InitVector[i] = 0x00000000;
                SEEDCBCDecryptV20(abHeader, nHeaderStartIndex + 32, 1, Schedule, InitVector, s, 0);

                SEEDKeyExpansionV20(masterkey, Schedule);
                for (i = 0; i < 4; ++i)
                    InitVector[i] = 0x00000000;
                SEEDCBCDecryptV20(abHeader, nHeaderStartIndex + 48, 1, Schedule, InitVector, t, 0);
                break;
            default:
                break;
        }
        for (i = 0; i < 16; ++i) {
            if (s[i] != t[i])
                return 0;
        }
        anSessionKey[0] = GETN(s,  0);
        anSessionKey[1] = GETN(s,  4);
        anSessionKey[2] = GETN(s,  8);
        anSessionKey[3] = GETN(s, 12);

        return nPlainBytes;
    }

    /*
        Encrypt memory to memory
        @ Input : plain : plain text bytes
                  plainstartindex : index in "plain" from which to encrypt
                  plainlength : # of bytes to be encrypted
                  cipher : buffer for cipher text
                  cipherstartindex : index in "cipher" from which to write encrypted text bytes
                  passphrase : user passphrase
                  algorithm : 1 ==> "Rijndael"
        @ Output: cipher : holds cipher text
        @ Return: 0 <== if "plain" or "plainstartindex" or "plainlength" is not suitable
                  -1 <== if "algorithm" is not supported
                  -2 <== if "cipher" or "cipherstartindex" is not suitable
                  length (in bytes) of cipher text <== if successful
    */
    public static int MemEncryptV20
    (byte[] plain,
     int plainstartindex,
     int plainlength,
     byte[] cipher,
     int cipherstartindex,
     String passphrase,
     int algorithm)
    {
        if ((cipherstartindex < 0) ||
            (cipherstartindex >= cipher.length) ||
            (cipher.length - cipherstartindex < 2 * (plainlength + 100)))
            return -2;
        if (algorithm != 1 && algorithm != 2)
            return -1;
        if ((plainlength <= 0) ||
            (plainstartindex < 0) ||
            (plainstartindex >= plain.length) ||
            (plain.length - plainstartindex < plainlength))
            return 0;

        int cipherlength = GetCipherLengthV20(plainlength);
        int[] sessionkey = new int[4];
        int[] Schedule = new int[4*11];
        int[/*4*/] InitVector = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        byte[] temp = new byte[cipher.length - cipherstartindex];
        MakeHeaderV20(algorithm, plainlength, passphrase.getBytes(), sessionkey, temp, 0);
        switch (algorithm) {
            case 1:
                RijndaelEncKeyExpansionV20(sessionkey, Schedule);
                RijndaelCBCEncryptV20(plain, plainstartindex, plainlength, Schedule, InitVector, temp, 4 * 16);
                break;
            case 2:
                SEEDKeyExpansionV20(sessionkey, Schedule);
                SEEDCBCEncryptV20(plain, plainstartindex, plainlength, Schedule, InitVector, temp, 4 * 16);
                break;
            default:
                break;
        }
        MakeFooterV20(temp, cipherlength - 16);

        ab2ax(temp, 0, cipherlength, cipher, cipherstartindex);
        return 2 * cipherlength;
    }

    /*
        Decrypt memory to memory
        @ Input : cipher : cipher text bytes
                  cipherstartindex : index in "cipher" from which to decrypt
                  cipherlength : # of bytes to be encrypted
                  plain : buffer for plain text
                  plainstartindex : index in "plain" from which to write decrypted text bytes
                  passphrase : user passphrase
        @ Output: plain : holds plain text
        @ Return: 0 <== if "cipher" or "cipherstartindex" or "cipherlength" is not suitable
                  -1 <== if "plain" or "plainstartindex" is not suitable
                  -2 <== if "passphrase" is incorrect
                  length (in bytes) of plain text <== if successful
    */
    public static int MemDecryptV20
    (byte[] cipher,
     int cipherstartindex,
     int cipherlength,
     byte[] plain,
     int plainstartindex,
     String passphrase)
    {
        if ((cipherlength <= 0) ||
            (cipherlength % (2 * 16) != 0) ||
            (cipherlength <= 2 * 4 * 16 + 2 * 16) ||
            (cipherstartindex < 0) ||
            (cipherstartindex >= cipher.length) ||
            (cipher.length - cipherstartindex < cipherlength))
            return 0;
        if ((plainstartindex < 0) ||
            (plainstartindex >= plain.length) ||
            (plain.length - plainstartindex < cipherlength))
            return -1;
        int nCipherBlockNum = (cipherlength - 2 * 4 * 16 - 2 * 16) / (2 * 16);

        /* Check integrity of cipher file and correctness of passphrase: */
        byte[] temp = new byte[cipherlength + 8];
        int ret = ax2ab(cipher, cipherstartindex, cipherlength / 2, temp, 0);
        if (ret == 0)
            return 0;
        int[] sessionkey = new int[4];
        int[] algorithm = new int[1];
        int nPlainBytes = CipherStatCheckV20(temp, 0,
                                             temp, cipherlength / 2 - 16,
                                             nCipherBlockNum,
                                             passphrase.getBytes(), sessionkey, algorithm);
        if (nPlainBytes == -1)
            return 0;
        else if (nPlainBytes == 0)
            return -2;

        /* Decrypt body: */
        int[] Schedule = new int[4*11];
        int[/*4*/] InitVector = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        switch (algorithm[0]) {
            case 1:
                RijndaelDecKeyExpansionV20(sessionkey, Schedule);
                RijndaelCBCDecryptV20(temp, 4 * 16,
                                      nCipherBlockNum, Schedule, InitVector,
                                      plain, plainstartindex);
                break;
            case 2:
                SEEDKeyExpansionV20(sessionkey, Schedule);
                SEEDCBCDecryptV20(temp, 4 * 16,
                                      nCipherBlockNum, Schedule, InitVector,
                                      plain, plainstartindex);
                break;
            default:
                break;
        }

        return nPlainBytes;
    }

    /*
        Encrypt file to file
        @ Input : ifname : plain text file name
                  ofname : cipher text file name
                  passphrase : user passphrase
                  algorithm : 1 ==> "Rijndael"
        @ Output: cipher text file with name "ofname"
        @ Return: 0 <== if "ifname" is empty
                  -1 <== if "ifname" cannot be read
                  -2 <== if "ofname" already exists or cannot be created
                  -3 <== if "algorithm" is not supported
                  1 <== if successful
    */
    public static int FileEncryptV20
    (String ifname,
     String ofname,
     String passphrase,
     int algorithm) throws IOException
    {
        if (algorithm != 1 && algorithm != 2)
            return -3;

        File ifile = new File(ifname);
        File ofile = new File(ofname);
        if (!ifile.canRead() || !ifile.isFile())
            return -1;
        if (ifile.length() == 0)
            return 0;
        if (ofile.exists())
            return -2;

        FileInputStream infile;
        FileOutputStream outfile;
        try {
            infile = new FileInputStream(ifile);
        } catch (IOException ex) {
            return -1;
        }
        try {
            outfile = new FileOutputStream(ofile);
        } catch (IOException ex) {
            infile.close();
            return -2;
        }

        /* Make header: */
        int[] sessionkey = new int[4];
        byte[] header = new byte[4 * 16 + 8];
        MakeHeaderV20(algorithm, 0, passphrase.getBytes(), sessionkey, header, 0);
        byte[] hexaheader = new byte[2 * 4 * 16 + 8];
        for (int i = 0; i < 2 * 4 * 16; ++i)
            hexaheader[i] = (byte)'0';
        try {
            outfile.write(hexaheader, 0, 2 * 4 * 16);
        } catch (IOException ex) {
            infile.close();
            outfile.close();
            ofile.delete();
            return -2;
        }

        /* Make body: */
        int[] Schedule = new int[4*11];
        int[/*4*/] InitVector = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        switch (algorithm) {
            case 1:
                RijndaelEncKeyExpansionV20(sessionkey, Schedule);
                break;
            case 2:
                SEEDKeyExpansionV20(sessionkey, Schedule);
                break;
            default:
                break;
        }
        byte[] plainbuff = new byte[BUFFSIZE + 1024];
        byte[] cipherbuff = new byte[BUFFSIZE + 1024];
        byte[] hexacipherbuff = new byte[2 * BUFFSIZE + 1024];
        int nPlainBytes = 0;
        int nBytesRead;
        while (true) {
            try {
                nBytesRead = infile.read(plainbuff, 0, BUFFSIZE);
            } catch (IOException ex) {
                infile.close();
                outfile.close();
                ofile.delete();
                return -1;
            }
            if (nBytesRead != BUFFSIZE)
                break;
            nPlainBytes += nBytesRead;
            switch (algorithm) {
                case 1:
                    RijndaelCBCEncryptV20(plainbuff, 0, BUFFSIZE, Schedule, InitVector, cipherbuff, 0);
                    try {
                        ab2ax(cipherbuff, 0, BUFFSIZE, hexacipherbuff, 0);
                        outfile.write(hexacipherbuff, 0, 2 * BUFFSIZE);
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                case 2:
                    SEEDCBCEncryptV20(plainbuff, 0, BUFFSIZE, Schedule, InitVector, cipherbuff, 0);
                    try {
                        ab2ax(cipherbuff, 0, BUFFSIZE, hexacipherbuff, 0);
                        outfile.write(hexacipherbuff, 0, 2 * BUFFSIZE);
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                default:
                    break;
            }
        }
        if (nBytesRead != -1) {
            nPlainBytes += nBytesRead;
            int n;
            switch (algorithm) {
                case 1:
                    n = 16 * RijndaelCBCEncryptV20(plainbuff, 0, nBytesRead, Schedule, InitVector, cipherbuff, 0);
                    try {
                        ab2ax(cipherbuff, 0, n, hexacipherbuff, 0);
                        outfile.write(hexacipherbuff, 0, 2 * n);
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                case 2:
                    n = 16 * SEEDCBCEncryptV20(plainbuff, 0, nBytesRead, Schedule, InitVector, cipherbuff, 0);
                    try {
                        ab2ax(cipherbuff, 0, n, hexacipherbuff, 0);
                        outfile.write(hexacipherbuff, 0, 2 * n);
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                default:
                    break;
            }
        }

        /* Make footer: */
        byte[] footer = new byte[16 + 8];
        MakeFooterV20(footer, 0);
        byte[] hexafooter = new byte[2 * 16 + 8];
        ab2ax(footer, 0, 16, hexafooter, 0);
        try {
            outfile.write(hexafooter, 0, 2 * 16);
        } catch (IOException ex) {
            infile.close();
            outfile.close();
            ofile.delete();
            return -2;
        }
        try {
            infile.close();
            outfile.close();
        } catch (IOException ex) {
            ofile.delete();
            return -2;
        }

        /* Correct plain file size: */
        PUTN(header, 20, nPlainBytes);
        ab2ax(header, 0, 4 * 16, hexaheader, 0);
        RandomAccessFile outfile2;
        try {
            outfile2 = new RandomAccessFile(ofile, "rw");
        } catch (IOException ex) {
            ofile.delete();
            return -2;
        }
        try {
            outfile2.seek(0L);
            outfile2.write(hexaheader, 0, 2 * 4 * 16);
        } catch (IOException ex) {
            outfile2.close();
            ofile.delete();
            return -2;
        }
        try {
            outfile2.close();
        } catch (IOException ex) {
            ofile.delete();
            return -2;
        }

        return 1;
    }

    /*
        Decrypt file to file
        @ Input : ifname : cipher text file name
                  ofname : plain text file name
                  passphrase : user passphrase
        @ Output: plain text file with name "ofname"
        @ Return: 0 <== if "ifname" is invalid
                  -1 <== if "ifname" cannot be read
                  -2 <== if "ofname" already exists or cannot be created
                  -3 <== if "passphrase" is incorrect
                  1 <== if successful
    */
    public static int FileDecryptV20
    (String ifname,
     String ofname,
     String passphrase) throws IOException
    {
        File ifile = new File(ifname);
        File ofile = new File(ofname);
        if (!ifile.canRead() || !ifile.isFile())
            return -1;
        long size = ifile.length();

        if ((size % (2 * 16) != 0) || (size <= 2 * 4 * 16 + 2 * 16))
            return 0;
        long bodysize = size - 2 * 4 * 16 - 2 * 16;
        
        //  현재 파일이 있는경우 복호화하지 않음. 
        if (ofile.exists())
           return -2;

        RandomAccessFile infile;
        FileOutputStream outfile;
        try {
            infile = new RandomAccessFile(ifile, "r");
        } catch (IOException ex) {
            return -1;
        }
        try {
            outfile = new FileOutputStream(ofile);
        } catch (IOException ex) {
            infile.close();
            return -2;
        }

        /* Check integrity of cipher file and correctness of passphrase: */
        int ret;
        byte[] hexaheader = new byte[2 * 4 * 16 + 8];
        byte[] hexafooter = new byte[2 * 16 + 8];
        byte[] header = new byte[4 * 16 + 8];
        byte[] footer = new byte[16 + 8];
        int[] sessionkey = new int[4];
        int[] algorithm = new int[1];
        try {
            infile.seek(0L);
            int a = infile.read(hexaheader, 0, 2 * 4 * 16);
            infile.seek(size - 32L);
            int b = infile.read(hexafooter, 0, 2 * 16);
            if (a != 2 * 4 * 16 || b != 2 * 16) {
                infile.close();
                outfile.close();
                ofile.delete();
                return -1;
            }
        } catch (IOException ex) {
            infile.close();
            outfile.close();
            ofile.delete();
            return -1;
        }
        ret = ax2ab(hexaheader, 0, 4 * 16, header, 0);
        if (ret == 0) {
            infile.close();
            outfile.close();
            ofile.delete();
            return 0;
        }
        ret = ax2ab(hexafooter, 0, 16, footer, 0);
        if (ret == 0) {
            infile.close();
            outfile.close();
            ofile.delete();
            return 0;
        }
        int nPlainBytes = CipherStatCheckV20(header, 0, footer, 0,
                                             (int)(bodysize / 32),
                                             passphrase.getBytes(),
                                             sessionkey, algorithm);
        if (nPlainBytes == -1) {
            infile.close();
            outfile.close();
            ofile.delete();
            return 0;
        } else if (nPlainBytes == 0) {
            infile.close();
            outfile.close();
            ofile.delete();
            return -3;
        }
        int missing = (int)(bodysize / 2 - (long)nPlainBytes);

        /* Decrypt body: */
        int[] Schedule = new int[4*11];
        int[] InitVector = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        switch (algorithm[0]) {
           case 1:
               RijndaelDecKeyExpansionV20(sessionkey, Schedule);
               break; 
           case 2:
               SEEDKeyExpansionV20(sessionkey, Schedule);
               break; 
           default:
               break;
        }
        byte[] hexacipherbuff = new byte[2 * BUFFSIZE + 1024];
        byte[] cipherbuff = new byte[BUFFSIZE + 1024];
        byte[] plainbuff = new byte[BUFFSIZE + 1024];
        int nBytesRead;
        long pos = (long)(2 * 4 * 16);
        try {
            infile.seek(pos);
        } catch (IOException ex) {
            infile.close();
            outfile.close();
            ofile.delete();
            return -1;
        }
        while (bodysize >= (long)(2 * BUFFSIZE)) {
            try {
                nBytesRead = infile.read(hexacipherbuff, 0, 2 * BUFFSIZE);
            } catch (IOException ex) {
                infile.close();
                outfile.close();
                ofile.delete();
                return -1;
            }
            if (nBytesRead != 2 * BUFFSIZE) {
                try {
                    infile.seek(pos);
                } catch (IOException ex) {
                    infile.close();
                    outfile.close();
                    ofile.delete();
                    return -1;
                }
                continue;
            }
            switch (algorithm[0]) {
                case 1:
                    ret = ax2ab(hexacipherbuff, 0, BUFFSIZE, cipherbuff, 0);
                    if (ret == 0) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return 0;
                    }
                    RijndaelCBCDecryptV20(cipherbuff, 0, BUFFSIZE / 16,
                                          Schedule, InitVector,
                                          plainbuff, 0);
                    try {
                        outfile.write(plainbuff, 0, BUFFSIZE);
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                case 2:
                    ret = ax2ab(hexacipherbuff, 0, BUFFSIZE, cipherbuff, 0);
                    if (ret == 0) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return 0;
                    }
                    SEEDCBCDecryptV20(cipherbuff, 0, BUFFSIZE / 16,
                                          Schedule, InitVector,
                                          plainbuff, 0);
                    try {
                        outfile.write(plainbuff, 0, BUFFSIZE);
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                default:
                    break;
            }
            pos += (long)(2 * BUFFSIZE);
            try {
                infile.seek(pos);
            } catch (IOException ex) {
                infile.close();
                outfile.close();
                ofile.delete();
                return -1;
            }
            bodysize -= (long)(2 * BUFFSIZE);
        }

        while (bodysize != 0) {
            try {
                nBytesRead = infile.read(hexacipherbuff, 0, (int)bodysize);
            } catch (IOException ex) {
                infile.close();
                outfile.close();
                ofile.delete();
                return -1;
            }
            if (nBytesRead != bodysize) {
                try {
                    infile.seek(pos);
                } catch (IOException ex) {
                    infile.close();
                    outfile.close();
                    ofile.delete();
                    return -1;
                }
                continue;
            }
            switch (algorithm[0]) {
                case 1:
                    ret = ax2ab(hexacipherbuff, 0, (int)(bodysize / 2), cipherbuff, 0);
                    if (ret == 0) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return 0;
                    }
                    RijndaelCBCDecryptV20(cipherbuff, 0, (int)(bodysize / 32),
                                          Schedule, InitVector,
                                          plainbuff, 0);
                    try {
                        outfile.write(plainbuff, 0, (int)(bodysize / 2 - missing));
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                case 2:
                    ret = ax2ab(hexacipherbuff, 0, (int)(bodysize / 2), cipherbuff, 0);
                    if (ret == 0) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return 0;
                    }
                    SEEDCBCDecryptV20(cipherbuff, 0, (int)(bodysize / 32),
                                          Schedule, InitVector,
                                          plainbuff, 0);
                    try {
                        outfile.write(plainbuff, 0, (int)(bodysize / 2 - missing));
                    } catch (IOException ex) {
                        infile.close();
                        outfile.close();
                        ofile.delete();
                        return -2;
                    }
                    break;
                default:
                    break;
            }
            bodysize = 0;
        }
        infile.close();
        outfile.close();

        return 1;
    }

    /*
        Encrypt memory to file
        @ Input : plain : plain text bytes
                  plainstartindex : index in "plain" from which to encrypt
                  plainlength : # of bytes to be encrypted
                  ofname : cipher text file name
                  passphrase : user passphrase
                  algorithm : 1 ==> "Rijndael"
        @ Output: cipher text file with name "ofname"
        @ Return: 0 <== if "plain" or "plainstartindex" or "plainlength" is not suitable
                  -1 <== if "ofname" already exists or cannot be created
                  -2 <== if "algorithm" is not supported
                  length (in bytes) of cipher text <== if successful
    */
    public static int Mem2FileEncryptV20
    (byte[] plain,
     int plainstartindex,
     int plainlength,
     String ofname,
     String passphrase,
     int algorithm) throws IOException
    {
        if (algorithm != 1 && algorithm != 2)
            return -2;
        if ((plainlength <= 0) ||
            (plainstartindex < 0) ||
            (plainstartindex >= plain.length) ||
            (plain.length - plainstartindex < plainlength))
            return 0;

        File ofile = new File(ofname);
       /* if (ofile.exists())
            return -1;
        */
        FileOutputStream outfile;
        try {
            outfile = new FileOutputStream(ofile);
        } catch (IOException ex) {
            return -1;
        }

        byte[] temp = new byte[plainlength + 100];
        int cipherlength = GetCipherLengthV20(plainlength);
        int[] sessionkey = new int[4];
        int[] Schedule = new int[4*11];
        int[/*4*/] InitVector = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        MakeHeaderV20(algorithm, plainlength, passphrase.getBytes(), sessionkey, temp, 0);
        switch (algorithm) {
            case 1:
                RijndaelEncKeyExpansionV20(sessionkey, Schedule);
                RijndaelCBCEncryptV20(plain, plainstartindex, plainlength, Schedule, InitVector, temp, 4 * 16);
                break;
            case 2:
                SEEDKeyExpansionV20(sessionkey, Schedule);
                SEEDCBCEncryptV20(plain, plainstartindex, plainlength, Schedule, InitVector, temp, 4 * 16);
                break;
            default:
                break;
        }
        MakeFooterV20(temp, cipherlength - 16);

        byte[] cipher = new byte[2 * (plainlength + 100)];
        ab2ax(temp, 0, cipherlength, cipher, 0);
        try {
            outfile.write(cipher, 0, 2 * cipherlength);
            outfile.close();
        } catch (IOException ex) {
            ofile.delete();
            return -1;
        }

        return 2 * cipherlength;
    }

    /*
        Decrypt file to memory
        @ Input : cipher text file name
                  plain : buffer for plain text
                  plainstartindex : index in "plain" from which to write decrypted text bytes
                  passphrase : user passphrase
        @ Output: plain : holds plain text
        @ Return: 0 <== if "ifname" is invalid
                  -1 <== if "ifname" cannot be read
                  -2 <== if "plain" or "plainstartindex" is not suitable
                  -3 <== if "passphrase" is incorrect
                  length (in bytes) of plain text <== if successful
    */
    public static int File2MemDecryptV20
    (String ifname,
     byte[] plain,
     int plainstartindex,
     String passphrase) throws IOException
    {
        File ifile = new File(ifname);
        if (!ifile.canRead() || !ifile.isFile())
            return -1;
        long size = ifile.length();
        if ((int)(size + 8) != size + 8)
            return 0;
        if ((size % (2 * 16) != 0) || (size <= 2 * 4 * 16 + 2 * 16))
            return 0;
        int nCipherBlockNum = (int)((size - 2 * 4 * 16 - 2 * 16) / (2 * 16));
        RandomAccessFile infile	= null;
        try {
            infile = new RandomAccessFile(ifile, "r");
            infile.seek(0L);
        } catch (IOException ex) {
        	try{if(infile!=null)infile.close();}catch(Exception e){infile=null;}
            return -1;
        }
        if ((long)(plain.length - plainstartindex) < size){
        	try{if(infile!=null)infile.close();}catch(Exception e){infile=null;}
            return -2;
        }

        byte[] cipher = new byte[(int)(size + 8)];
        while (true) {
            int nBytesRead;
            try {
                nBytesRead = infile.read(cipher, 0, (int)size);
            } catch (IOException ex) {
                infile.close();
                return -1;
            }
            if ((long)nBytesRead == size)
                break;
            try {
                infile.seek(0L);
            } catch (IOException ex) {
                infile.close();
                return -1;
            }
        }
        infile.close();

        /* Check integrity of cipher file and correctness of passphrase: */
        byte[] temp = new byte[(int)(size / 2 + 8)];
        int ret = ax2ab(cipher, 0, (int)(size / 2), temp, 0);
        if (ret == 0)
            return 0;
        int[] sessionkey = new int[4];
        int[] algorithm = new int[1];
        int nPlainBytes = CipherStatCheckV20(temp, 0,
                                             temp, (int)(size / 2 - 16),
                                             nCipherBlockNum,
                                             passphrase.getBytes(), sessionkey, algorithm);
        if (nPlainBytes == -1)
            return 0;
        else if (nPlainBytes == 0)
            return -3;

        /* Decrypt body: */
        int[] Schedule = new int[4*11];
        int[/*4*/] InitVector = {0x00000000, 0x00000000, 0x00000000, 0x00000000};
        switch (algorithm[0]) {
            case 1:
                RijndaelDecKeyExpansionV20(sessionkey, Schedule);
                RijndaelCBCDecryptV20(temp, 4 * 16,
                                      nCipherBlockNum, Schedule, InitVector,
                                      plain, plainstartindex);
                break;
            case 2:
                SEEDKeyExpansionV20(sessionkey, Schedule);
                SEEDCBCDecryptV20(temp, 4 * 16,
                                      nCipherBlockNum, Schedule, InitVector,
                                      plain, plainstartindex);
                break;
            default:
                break;
        }

        return nPlainBytes;
    }
}
